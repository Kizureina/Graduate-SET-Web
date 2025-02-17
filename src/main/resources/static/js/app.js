const { createApp } = Vue;
const { ElInput, ElButton } = ElementPlus;

const app = createApp({
    data() {
        return {
            searchQuery: "",
            cart: [],
            categories: [
                { key: "prime", title: "Prime Video", description: "免费 30 天试用，海量影片随时观看。", image: "https://s21.ax1x.com/2025/02/16/pEKIu9J.png" },
                { key: "fashion", title: "热门时尚", description: "女装、男装、美妆、家居好物。", image: "https://s21.ax1x.com/2025/02/16/pEKIABV.png" },
                { key: "discount", title: "特价折扣", description: "畅销商品限时特价，低至 50% 折扣。", image: "https://s21.ax1x.com/2025/02/16/pEKImh4.png" },
                { key: "electronics", title: "电子产品", description: "手机、笔记本电脑、耳机，最新科技一网打尽。", image: "https://s21.ax1x.com/2025/02/16/pEKIC1s.png" },
                { key: "toys", title: "儿童玩具", description: "益智玩具、毛绒玩具、拼图等，孩子的欢乐天地。", image: "https://s21.ax1x.com/2025/02/16/pEKIeNF.png" },
                { key: "home", title: "家居生活", description: "厨房电器、智能家居，提升生活品质。", image: "https://s21.ax1x.com/2025/02/16/pEKIE7T.png" },
                { key: "health", title: "健康养生", description: "健身器材、健康食品，让生活更健康。", image: "https://s21.ax1x.com/2025/02/16/pEKIQj1.png" },
                { key: "smart", title: "智能设备", description: "智能手表、智能音箱，未来科技触手可及。", image: "https://s21.ax1x.com/2025/02/16/pEKIK39.png" }
            ]
        };
    },
    methods: {
        searchProduct() {
            alert("搜索：" + this.searchQuery);
        },
        viewCategory(categoryKey) {
            window.location.href = `product.html?category=${categoryKey}`;
        }
    }
});

app.component("el-input", ElInput);
app.component("el-button", ElButton);
app.mount("#app");