#!/bin/bash

# 目录树显示脚本，避免无限递归

# 默认设置
MAX_DEPTH=15           # 默认最大递归深度
SHOW_HIDDEN=false     # 默认不显示隐藏文件
SHOW_SYMLINKS=true    # 默认显示符号链接
BASE_DIR="${1:-.}"    # 默认从当前目录开始

# 使用帮助
usage() {
    echo "用法: $0 [选项] [目录]"
    echo "选项:"
    echo "  -d NUM  设置最大显示深度 (默认: 3)"
    echo "  -a      显示隐藏文件"
    echo "  -L      不跟踪符号链接"
    echo "  -h      显示此帮助信息"
    exit 0
}

# 解析参数
while getopts "d:aLh" opt; do
    case $opt in
        d) MAX_DEPTH="$OPTARG" ;;
        a) SHOW_HIDDEN=true ;;
        L) SHOW_SYMLINKS=false ;;
        h) usage ;;
        *) usage ;;
    esac
done
shift $((OPTIND-1))

# 如果指定了目录参数，则使用它
if [ $# -gt 0 ]; then
    BASE_DIR="$1"
fi

# 检查目录是否存在
if [ ! -d "$BASE_DIR" ]; then
    echo "错误: 目录 '$BASE_DIR' 不存在" >&2
    exit 1
fi

# 显示目录树函数
display_tree() {
    local dir="$1"
    local depth="$2"
    local indent="$3"
    
    # 检查是否达到最大深度
    if [ "$depth" -gt "$MAX_DEPTH" ]; then
        echo "${indent}└── [深度超过限制...]"
        return
    fi
    
    # 获取目录内容并排序
    local items=()
    if [ "$SHOW_HIDDEN" = true ]; then
        items=("$dir"/* "$dir"/.*)
    else
        items=("$dir"/*)
    fi
    
    # 处理每个项目
    local count=0
    local total=0
    for item in "${items[@]}"; do
        # 跳过 . 和 ..
        [ "$item" = "$dir/." ] || [ "$item" = "$dir/.." ] && continue
        # 如果隐藏文件不显示，则跳过
        [ "$SHOW_HIDDEN" = false ] && [[ "$(basename "$item")" = .* ]] && continue
        total=$((total+1))
    done
    
    for item in "${items[@]}"; do
        # 跳过 . 和 ..
        [ "$item" = "$dir/." ] || [ "$item" = "$dir/.." ] && continue
        # 如果隐藏文件不显示，则跳过
        [ "$SHOW_HIDDEN" = false ] && [[ "$(basename "$item")" = .* ]] && continue
        
        count=$((count+1))
        local name="$(basename "$item")"
        
        # 判断是否是最后一个项目
        if [ $count -eq $total ]; then
            local branch="└── "
            local next_indent="$indent    "
        else
            local branch="├── "
            local next_indent="$indent│   "
        fi
        
        # 显示项目名称
        echo "${indent}${branch}${name}"
        
        # 如果是目录且不是符号链接(或允许跟踪符号链接)，则递归
        if [ -d "$item" ] && ( [ ! -L "$item" ] || [ "$SHOW_SYMLINKS" = true ] ); then
            display_tree "$item" $((depth+1)) "$next_indent"
        fi
    done
}

# 显示基础目录信息
echo "显示目录: $BASE_DIR (最大深度: $MAX_DEPTH)"
display_tree "$BASE_DIR" 1 ""
