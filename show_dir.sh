#!/bin/bash

DIR=${1:-.}
declare -A visited_inodes

# 获取 inode（唯一标识）函数
get_inode() {
    stat -c "%d-%i" "$1" 2>/dev/null
}

print_tree() {
    local prefix="$1"
    local dir="$2"

    local inode=$(get_inode "$dir")
    if [[ -n "${visited_inodes[$inode]}" ]]; then
        echo "${prefix}↪ [循环链接] $(basename "$dir")"
        return
    fi
    visited_inodes[$inode]=1

    local items=()
    while IFS= read -r -d $'\0' entry; do
        items+=("$entry")
    done < <(find "$dir" -mindepth 1 -maxdepth 1 -print0 | sort -z)

    local count=${#items[@]}
    for ((i=0; i<count; i++)); do
        local item="${items[$i]}"
        local basename=$(basename "$item")

        if [ $i -eq $((count-1)) ]; then
            echo "${prefix}└── $basename"
            new_prefix="${prefix}    "
        else
            echo "${prefix}├── $basename"
            new_prefix="${prefix}│   "
        fi

        if [ -d "$item" ] && [ ! -L "$item" ]; then
            print_tree "$new_prefix" "$item"
        fi
    done
}

echo "$DIR"
print_tree "" "$DIR"


# 统计文件中代码行数

# find . -type f -exec wc -l {} +

