<template>
  <div class="container">
    <Breadcrumb :items="['menu.map', 'menu.map.index']" />
    <a-card class="general-card" title="地图">
      <a-row>
        <a-col :flex="1">
          <div class="map-container">
            <div class="map-placeholder">
              <a-empty description="地图组件">
                <template #image>
                  <icon-map
                    style="font-size: 48px; color: var(--color-text-3)"
                  />
                </template>
                <a-button type="primary">加载地图</a-button>
              </a-empty>
            </div>
          </div>
        </a-col>
        <a-col :flex="'0 0 300px'" style="margin-left: 16px">
          <a-card title="地图控制" size="small">
            <a-space direction="vertical" fill>
              <a-button type="primary" long>
                <template #icon>
                  <icon-location />
                </template>
                定位当前位置
              </a-button>
              <a-button long>
                <template #icon>
                  <icon-search />
                </template>
                搜索地点
              </a-button>
              <a-button long>
                <template #icon>
                  <icon-star />
                </template>
                收藏位置
              </a-button>
            </a-space>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import Breadcrumb from '@/components/breadcrumb/index.vue';
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
  }

  .general-card {
    min-height: 500px;
  }

  .map-container {
    height: 500px;
    border: 1px solid var(--color-border);
    border-radius: 6px;
    overflow: hidden;
  }

  .map-placeholder {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--color-fill-2);
  }
</style>
