<template>
  <DivCustom label="Danh sách phòng" customClasses="mt-5">
    <div class="min-h-[300px]">
      <a-table
        :columns="columns"
        :data-source="rooms"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['5', '10', '20']
        }"
        :scroll="{ y: 400 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'stt'">
            {{ rooms.indexOf(record) + 1 }}
          </template>

          <template v-if="column.key === 'giaHienTai'">
            {{ formatCurrency(record.giaHienTai) }}
          </template>

          <template v-if="column.key === 'trangThaiPhong'">
            <a-tag :color="getRoomStatusColor(record.trangThaiPhong)">
              {{ getRoomStatusText(record.trangThaiPhong) }}
            </a-tag>
          </template>
         <template v-if="column.key === 'operation'">
            <div class="flex gap-1 items-center justify-center text-center">
            
              <a-tooltip title="Xóa phòng">
                <a-button
                  @click="handleDeleteClick(record.id)"
                  class="flex items-center justify-center w-8 h-8 "
                 style="
    background-color: #fee2e2 !important;
    color: #d81a6c !important;
    border: none !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  "
                >
                  <DeleteOutlined />
                </a-button>
              </a-tooltip>

             
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustomTable.vue'
import type { TableColumnsType } from 'ant-design-vue'
import { defineEmits, defineProps } from 'vue'
import {
  DeleteOutlined
} from '@ant-design/icons-vue'
defineProps<{
  rooms: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const emit = defineEmits(['page-change','delete'])

const columns: TableColumnsType = [
  { title: 'STT', key: 'stt', dataIndex: 'stt', width: 80, align: 'center' },
  { title: 'Mã phòng', key: 'maPhong', dataIndex: 'maPhong', width: 120, align: 'center' },
  { title: 'Tên phòng', key: 'tenPhong', dataIndex: 'tenPhong', width: 150, align: 'center' },
  { title: 'Loại phòng', key: 'loaiPhong', dataIndex: 'loaiPhong', width: 120, align: 'center' },
  { title: 'Giá hiện tại', key: 'giaHienTai', dataIndex: 'giaHienTai', width: 120, align: 'center' },
  { title: 'Sức chứa', key: 'sucChua', dataIndex: 'sucChua', width: 100, align: 'center' },
  { title: 'Trạng thái', key: 'trangThaiPhong', dataIndex: 'trangThaiPhong', width: 130, align: 'center' },
   {
    title: 'Hành động',
    key: 'operation',
    width: 130,
    align: 'center',
    fixed: 'right'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}
const handleDeleteClick = (id: string) => {
  emit('delete', id)
}

const formatCurrency = (value: number) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)

const getRoomStatusText = (status: string | number) => {
  switch (status) {
    case 'TRONG':
    case 0:
      return 'Trống'
    case 'DA_DAT':
    case 1:
      return 'Đã đặt'
    case 'DANG_SU_DUNG':
    case 2:
      return 'Đang sử dụng'
    default:
      return 'Không xác định'
  }
}

const getRoomStatusColor = (status: string | number) => {
  switch (status) {
    case 'TRONG':
    case 0:
      return 'green'
    case 'DA_DAT':
    case 1:
      return 'orange'
    case 'DANG_SU_DUNG':
    case 2:
      return 'red'
    default:
      return 'default'
  }
}

</script>

<style scoped>
:deep(.ant-table) {
  font-size: 13px;
}
</style>
