<template>
  <div class="container">
    <Breadcrumb :items="['menu.map', 'menu.map.marker']" />
    <a-card class="general-card" title="地图标记">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-card title="标记列表" size="small">
            <template #extra>
              <a-button
                type="primary"
                size="small"
                @click="showAddModal = true"
              >
                <template #icon>
                  <icon-plus />
                </template>
                添加标记
              </a-button>
            </template>
            <a-list :data="markers" size="small">
              <template #item="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #avatar>
                      <a-avatar :style="{ backgroundColor: item.color }">
                        <icon-location />
                      </a-avatar>
                    </template>
                    <template #title>
                      <a @click="selectMarker(item)">{{ item.name }}</a>
                    </template>
                    <template #description>
                      {{ item.description }}
                    </template>
                  </a-list-item-meta>
                  <template #actions>
                    <a-button
                      type="text"
                      size="small"
                      @click="editMarker(item)"
                    >
                      <icon-edit />
                    </a-button>
                    <a-button
                      type="text"
                      size="small"
                      @click="deleteMarker(item)"
                    >
                      <icon-delete />
                    </a-button>
                  </template>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
        <a-col :span="16">
          <div class="map-container">
            <div class="map-placeholder">
              <a-empty description="地图标记组件">
                <template #image>
                  <icon-map
                    style="font-size: 48px; color: var(--color-text-3)"
                  />
                </template>
                <a-space>
                  <a-button type="primary">加载地图</a-button>
                  <a-button>显示标记</a-button>
                </a-space>
              </a-empty>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 添加/编辑标记模态框 -->
    <a-modal
      v-model:visible="showAddModal"
      :title="editingMarker ? '编辑标记' : '添加标记'"
      @ok="saveMarker"
      @cancel="cancelEdit"
    >
      <a-form :model="markerForm" layout="vertical">
        <a-form-item label="标记名称" required>
          <a-input v-model="markerForm.name" placeholder="请输入标记名称" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea
            v-model="markerForm.description"
            placeholder="请输入标记描述"
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="标记颜色">
          <a-color-picker v-model="markerForm.color" />
        </a-form-item>
        <a-form-item label="坐标">
          <a-row :gutter="8">
            <a-col :span="12">
              <a-input-number
                v-model="markerForm.lat"
                placeholder="纬度"
                :precision="6"
                style="width: 100%"
              />
            </a-col>
            <a-col :span="12">
              <a-input-number
                v-model="markerForm.lng"
                placeholder="经度"
                :precision="6"
                style="width: 100%"
              />
            </a-col>
          </a-row>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import Breadcrumb from '@/components/breadcrumb/index.vue';

  interface Marker {
    id: string;
    name: string;
    description: string;
    color: string;
    lat: number;
    lng: number;
  }

  const markers = ref<Marker[]>([
    {
      id: '1',
      name: '公司总部',
      description: '公司主要办公地点',
      color: '#165DFF',
      lat: 39.9042,
      lng: 116.4074,
    },
    {
      id: '2',
      name: '研发中心',
      description: '技术研发部门',
      color: '#00B42A',
      lat: 39.9142,
      lng: 116.4174,
    },
    {
      id: '3',
      name: '客户服务中心',
      description: '客户服务和支持',
      color: '#F7BA1E',
      lat: 39.8942,
      lng: 116.3974,
    },
  ]);

  const showAddModal = ref(false);
  const editingMarker = ref<Marker | null>(null);
  const markerForm = reactive({
    name: '',
    description: '',
    color: '#165DFF',
    lat: 39.9042,
    lng: 116.4074,
  });

  const selectMarker = (marker: Marker) => {
    Message.info(`选中标记: ${marker.name}`);
  };

  const editMarker = (marker: Marker) => {
    editingMarker.value = marker;
    Object.assign(markerForm, marker);
    showAddModal.value = true;
  };

  const deleteMarker = (marker: Marker) => {
    const index = markers.value.findIndex((m) => m.id === marker.id);
    if (index > -1) {
      markers.value.splice(index, 1);
      Message.success('删除成功');
    }
  };

  const saveMarker = () => {
    if (!markerForm.name.trim()) {
      Message.error('请输入标记名称');
      return;
    }

    if (editingMarker.value) {
      // 编辑现有标记
      const index = markers.value.findIndex(
        (m) => m.id === editingMarker.value!.id
      );
      if (index > -1) {
        Object.assign(markers.value[index], markerForm);
      }
      Message.success('编辑成功');
    } else {
      // 添加新标记
      const newMarker: Marker = {
        id: Date.now().toString(),
        ...markerForm,
      };
      markers.value.push(newMarker);
      Message.success('添加成功');
    }

    cancelEdit();
  };

  const cancelEdit = () => {
    showAddModal.value = false;
    editingMarker.value = null;
    Object.assign(markerForm, {
      name: '',
      description: '',
      color: '#165DFF',
      lat: 39.9042,
      lng: 116.4074,
    });
  };
</script>

<style scoped lang="less">
  .container {
    padding: 0 20px 20px 20px;
  }

  .general-card {
    min-height: 600px;
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
