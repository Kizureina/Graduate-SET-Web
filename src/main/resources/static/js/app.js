const { createApp } = Vue;
const { ElInput, ElButton } = ElementPlus;

const app = createApp({
    data() {
        return {
            searchQuery: "",
            cart: [],
            categories: [
                {
                    title: "Prime Video",
                    description: "免费 30 天试用，海量影片随时观看。",
                    image: "https://source.unsplash.com/400x250/?movie",
                },
                {
                    title: "热门时尚",
                    description: "女装、男装、美妆、家居好物。",
                    image: "https://source.unsplash.com/400x250/?fashion",
                },
                {
                    title: "特价折扣",
                    description: "畅销商品限时特价，低至 50% 折扣。",
                    image: "https://source.unsplash.com/400x250/?sale",
                },
                {
                    title: "电子产品",
                    description: "手机、笔记本电脑、耳机，最新科技一网打尽。",
                    image: "https://source.unsplash.com/400x250/?electronics",
                },
                {
                    title: "儿童玩具",
                    description: "益智玩具、毛绒玩具、拼图等，孩子的欢乐天地。",
                    image: "https://source.unsplash.com/400x250/?toys",
                },
                {
                    title: "家居生活",
                    description: "厨房电器、智能家居，提升生活品质。",
                    image: "https://source.unsplash.com/400x250/?home",
                },
                {
                    title: "健康养生",
                    description: "健身器材、健康食品，让生活更健康。",
                    image: "https://source.unsplash.com/400x250/?health",
                },
                {
                    title: "智能设备",
                    description: "智能手表、智能音箱，未来科技触手可及。",
                    image: "https://source.unsplash.com/400x250/?smart",
                }
            ]
        };
    },
    methods: {
        searchProduct() {
            alert("搜索：" + this.searchQuery);
        },
        viewCategory(title) {
            alert("进入分类：" + title);
        }
    }
});
// **手动注册组件**
app.component("el-input", ElInput);
app.component("el-button", ElButton);

app.mount("#app");
