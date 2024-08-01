package com.test.t6;

import java.util.Random;

/**
 * 随机图片 图床
 * https://imgimg.cc/upload
 */
public class RandomImageUrl {
    public static String random() {
        String[] imageUrls = {
                "https://cdn-fusion.imgimg.cc/i/2024/9c14c366570fef7f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1cc0d033f7f6b43b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b06fadeb1c249689.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/f4c9bfba532225b4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/12d42f805ba75c34.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/039b79813690b80b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/80b23a9d82fdc052.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5daaee15e4ee282e.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a0ca7ec414c34f97.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1f8d808894d3d6fa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ee903970249d10b6.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e96a561aeaaaa84f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ea11e434b2ffc98f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5c81ac9041f05d2c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/0938ee2d6f67a135.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b3a2ee741b22aa4c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/d7b9c8a60ff9f625.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/8f87d7202a87840c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6604126d15a1f05a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5fbe0dea0643bbf5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5d11c60eed9e0909.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a3d1d72f2f281df3.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/68b38e43483e9fe8.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/c6dba0b3c086eca7.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5941fde4256a7e60.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/07f4ca2bf8c0ce8a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/f2740d7b40ac8dce.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/8810a161f5873fe1.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/2f6178fa7e21d9f4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/da9a89a78f74cebc.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6e55f7c3d0971e7c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/4b0e200e5d48e2fa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b148113efdd054a0.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/4dac85081c35d079.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/9776246b9018e115.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e93714648c035e42.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a864f1ab308e98aa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/8b2f7cadf91055e4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/cfb7c03c17f839ce.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1a52f1a38316fb60.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/827c240460a00d2a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/fa2674d55bde3aff.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5bb2347b66f67814.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/2693413bec6e8890.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/3cb3e46eaba98198.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/77f14c5acda5e1ad.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/16ebe0770231eccc.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/fd84b9510558a238.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/dee6b4656b545d8f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/923307ad4109d56b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/c51e5ff80a059954.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b9884cdb6162455c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/cd84a93368042646.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/c5041a6bea4defb2.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b084735f9f786f54.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/63b9d7c1be27d97e.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6108d3c45776a0ee.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/d5a125d8e9f810f7.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/027c17f9fd007b1b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e807e68d3411fe8e.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ca01f52b0212f1f5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5f9ff82aba45e17c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/2d520fbd26a87b35.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a9bc53a32cbd2881.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/729224a20db2cd9a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/823ba44291f5c14a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/307dcaf2c1f22c15.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/59d6bc0bc9f1c7fa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1bf49a35106e9ada.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6ef2b3ecd3c2d389.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/d5dc05458a01cbb4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/543a930f2b20e597.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/9a3e81c02371a709.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/caf90b43a666fb74.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/adc42a09fd7883be.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5f8ea9c07328765a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/751ec4a4eaa7fd17.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6f6a59f7238396b5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/436074966ed2ea0b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/4d4e1c36abad9be7.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/293a0fb80a995b61.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5e5b90f2646ec437.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/fc3cde636a63208f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1d713377a33035e5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/924eb46452da491d.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e2268b9e0f3c28f3.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/7b2ff31aef79e683.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ea899b5becd9db46.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/df77d31718fb8bc9.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/658992b538532e43.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/87e8e10b2c90352a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/68bb898f6f721135.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/94aa2b3778fd81e3.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/42e28ccccc228216.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5b25865cf1586af4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e242dfe6f636823f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/02e8fc07a3a2e650.jpg"
        };

        // 随机生成一个索引
        Random random = new Random();
        int index = random.nextInt(imageUrls.length);

        // 获取随机的图片链接
        String randomImageUrl = imageUrls[index];
        System.out.println("随机获取的图片链接：" + randomImageUrl);
        return randomImageUrl;
    }
}
