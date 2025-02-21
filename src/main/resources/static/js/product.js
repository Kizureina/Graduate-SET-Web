const { createApp } = Vue;
const { ElInput, ElButton } = ElementPlus;

const products = {
    "prime": {
        name: "Prime Video 会员",
        description: "作为全球最大的在线视频平台之一，Prime Video 为您提供海量影视内容，涵盖了最新的电影、电视剧、纪录片等多种类型。无论是热门大片还是经典剧集，Prime Video 都能满足您的不同观影需求。现在，您可以体验30天的免费试用，无需任何费用，畅享平台上所有内容。无广告干扰的流畅播放体验，让您在家就能享受影院级别的娱乐体验。选择Prime Video，您将永远不会错过任何精彩时刻。",
        price: 29,
        image: "https://s21.ax1x.com/2025/02/16/pEKIu9J.png"
    },
    "fashion": {
        name: "时尚潮流服饰",
        description: "这里汇集了全球时尚品牌的最新款服饰，紧跟潮流，精选优质面料，时尚与舒适并存。无论是日常休闲穿搭，还是正式场合的精致打扮，您都能找到心仪的单品。从经典款到最新流行趋势，我们精心挑选每一件商品，确保每一位顾客都能展现最自信、最时尚的一面。以合理的价格，让您与时尚接轨，随时走在潮流前端。",
        price: 199,
        image: "https://s21.ax1x.com/2025/02/16/pEKIABV.png"
    },
    "discount": {
        name: "限时折扣商品",
        description: "如果您喜欢折扣购物，这一精选特价商品绝对不可错过！我们特别挑选了各类热门商品进行限时优惠，价格低至五折。无论是家居用品、电子产品还是时尚服饰，都能在这里找到极具性价比的商品。折扣期间，不仅有实惠价格，还能享受更快速的配送服务。优惠时间有限，赶快抓住机会，尽享购物乐趣！",
        price: 99,
        image: "https://s21.ax1x.com/2025/02/16/pEKImh4.png"
    },
    "electronics": {
        name: "高端电子设备",
        description: "最新款手机、电脑、耳机等高端电子产品，为您带来最前沿的科技体验。无论是高清晰度的显示屏，还是强劲的处理器，这些电子设备都经过严格筛选，确保每一款都能满足消费者对高品质的追求。作为智能生活的必备工具，它们不仅提升您的工作效率，还能带来极致的娱乐享受。赶紧升级您的电子设备，感受科技带来的无限可能。",
        price: 2999,
        image: "https://s21.ax1x.com/2025/02/16/pEKIC1s.png"
    },
    "toys": {
        name: "益智儿童玩具",
        description: "为孩子挑选玩具时，安全性和教育性是我们最关注的因素。我们精心挑选了各类益智儿童玩具，这些玩具不仅采用无毒环保材料，还能够激发孩子的创造力、想象力和动手能力。通过玩耍，孩子可以在轻松愉快的氛围中获得成长，培养逻辑思维和解决问题的能力。选择这些玩具，让孩子的成长更加多姿多彩，寓教于乐。",
        price: 79,
        image: "https://s21.ax1x.com/2025/02/16/pEKIeNF.png"
    },
    "home": {
        name: "家居生活必备",
        description: "在忙碌的生活中，家居用品往往是提升生活质量的关键。我们提供一系列智能家居产品及厨房用品，帮助您打造更加便捷、舒适的家庭环境。从智能家居设备到厨房神器，所有商品均为高品质、易操作的设计，极大地提高了家庭生活的效率和乐趣。无论您是想要提升家庭安全，还是改善居住舒适度，我们都有完美的解决方案。",
        price: 249,
        image: "https://s21.ax1x.com/2025/02/16/pEKIE7T.png"
    },
    "health": {
        name: "健康养生食品",
        description: "现代人越来越注重饮食健康，选择优质的健康食品成为了一种趋势。我们提供各种营养丰富的健康食品，专为注重身心健康的消费者设计。精选天然原料，经过严格筛选，确保每一款产品都能为您的健康加分。无论是提高免疫力、改善消化，还是养生保健，我们的健康食品都能为您提供全面的支持，助力您保持最佳状态，享受健康的每一天。",
        price: 159,
        image: "https://s21.ax1x.com/2025/02/16/pEKIQj1.png"
    },
    "smart": {
        name: "前沿智能设备",
        description: "无论是智能手表、智能音箱，还是其他高科技产品，我们的前沿智能设备将科技与生活紧密结合。通过与智能设备的互动，您不仅可以轻松管理日常事务，还能享受更加高效、便捷的生活方式。精准的健康监测、丰富的娱乐体验和智能化的家庭控制，让这些设备成为现代生活的得力助手。选择我们的智能设备，体验未来生活的无限可能。",
        price: 699,
        image: "https://s21.ax1x.com/2025/02/16/pEKIK39.png"
    }
};

function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

const app = createApp({
    components: {
        ElInput,
        ElButton
    },
    data() {
        return {
            username: "", // 存储用户名
            searchQuery: '',
            cart: [],
            product: products[getQueryParam("category")] || {
                name: "未知商品",
                description: "未找到匹配的商品信息",
                price: "N/A",
                image: "https://www.helloimg.com/i/2025/02/16/67b1ad5d61fa4.png"
            },
            showModal: false,
            selectedOptions: [],
            options: [
                { value: "color_black", label: "黑色" },
                { value: "color_white", label: "白色" },
                { value: "l", label: "L" },
                { value: "xl", label: "XL" },
                { value: "xxl", label: "XXL" },
                { value: "xxxl", label: "XXXL" },
            ],
            showPaymentModal: false,
            selectedPayment: "",
            payOptons: [
                {
                    value: "wechat", label: "微信"
                },
                {
                    value: "zhi", label: "支付宝"
                },
                {
                    value: "bank", label: "银行卡"
                }
            ],
            selectedPayments: ""
        };
    },
    mounted() {
        this.fetchUsername();
    },
    methods: {
        searchProduct() {
            console.log('搜索:', this.searchQuery);
            alert("搜索商品：" + this.searchQuery);
            // 执行搜索逻辑
        },
        addToCart() {
            this.cart.push(this.product);
            console.log('已加入购物车:', this.product.name);
        },
        buyNow() {
            alert('立即购买: ' + this.product.name);
            // 执行立即购买逻辑
            // axios.post("/api/login", { username, password })
            //     .then(response => {
            //         alert(response.data.message); // 提示登录信息
            //         if (response.data.status === "success") {
            //             window.location.href = "/index.html"; // 跳转到主页
            //         }
            //     })
            //     .catch(error => {
            //         console.error("请求失败", error);
            //     });
        },
        openModal() {
            this.showModal = true;
        },
        closeModal() {
            this.showModal = false;
            this.selectedOptions = [];
        },

        goToCart() {
            alert('跳转到购物车');
            // 跳转到购物车页面的逻辑
        },
        openPaymentModal() {
            this.showPaymentModal = true;
            this.selectedPayment = true;
        },
        closePaymentModal() {
            this.showPaymentModal = false;
            this.selectedPayment = "";
        },
        async submitPayment() {
            if (!this.selectedPayment) {
                alert("请选择支付方式！");
                return;
            }

            try {
                let request = JSON.stringify(
                    {
                        paymentMethod: this.selectedPayments,
                        productName: this.product.name,
                        productType: this.selectedOptions,
                        userName: this.username
                    });
                /*
                * 注意此处交互请求的逻辑不可以对await和.then混用
                * */
                fetch("/api/pay", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: request
                })
                    .then(response => response.json())  // 解析 JSON
                    .then(data => {
                        alert("支付成功: " + JSON.stringify(data));
                        this.closePaymentModal();
                        this.closeModal();
                    })
                    .catch(error => console.error("支付请求失败:", error));
                //console.log(request);

            } catch (error) {
                console.error("支付失败", error);
            }
        },
        fetchUsername() {
            fetch("/api/username")
                .then(response => response.text()) // 假设 API 返回的是纯字符串
                .then(data => {
                    this.username = data;
                    console.log("用户名" + this.username);
                })
                .catch(error => {
                    console.error("获取用户名失败:", error);
                });
        }
    }
});

app.component("el-input", ElInput);
app.component("el-button", ElButton);
app.mount('#app');