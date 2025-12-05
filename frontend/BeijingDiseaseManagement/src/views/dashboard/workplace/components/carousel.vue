<template>
  <a-carousel
    indicator-type="slider"
    show-arrow="hover"
    auto-play
    style="width: 100%; height: 170px; border-radius: 4px; overflow: hidden"
  >
    <a-carousel-item v-for="(item, idx) in carouselItems" :key="idx">
      <div style="position: relative; height: 170px">
        <img
          :src="item.src"
          style="width: 100%; height: 100%; object-fit: cover"
        />
        <div class="carousel-caption">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </div>
    </a-carousel-item>
  </a-carousel>
</template>

<script lang="ts" setup>
  const carouselItems = [
    {
      src: '//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/5cc3cd1d994b7ef9db6a1f619a22addd.jpg~tplv-49unhts6dw-image.image',
      title: '健康体检',
      description: '专业医疗团队，全面健康检查',
    },
    {
      src: '//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/f256cbcc287139e191fecea9d255a1f0.jpg~tplv-49unhts6dw-image.image',
      title: '专家门诊',
      description: '名医专家，精准诊疗',
    },
    {
      src: '//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/b557ff0cd44146a2e471b477af2f30d0.jpg~tplv-49unhts6dw-image.image',
      title: '急诊急救',
      description: '24小时急诊服务，守护生命',
    },
    {
      src: '//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/665106f4bbd2a2df96eaf7aec52f7bc3.jpg~tplv-49unhts6dw-image.image',
      title: '康复治疗',
      description: '专业康复，重获健康',
    },
    {
      src: '//p3-armor.byteimg.com/tos-cn-i-49unhts6dw/ea095a2c9c72b5d8f2f2818040db736d.jpg~tplv-49unhts6dw-image.image',
      title: '预防保健',
      description: '预防为主，健康生活',
    },
  ];
</script>

<style scoped lang="less">
  .carousel-caption {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
    color: white;
    padding: 20px;
    text-align: center;

    h3 {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 600;
    }

    p {
      margin: 0;
      font-size: 14px;
      opacity: 0.9;
    }
  }
</style>
