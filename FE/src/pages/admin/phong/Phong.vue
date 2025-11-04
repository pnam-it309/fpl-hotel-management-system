<template>
  <div class="p-4">
    <PhongTable :rooms="rooms" :pagination-params="paginationParams" :total-items="totalItems"
      @page-change="handlePageChange"
      @delete="handleDeleteClick"
      
      />
      
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PhongTable from './PhongTable.vue'
import { deleteRoom, getRooms } from '@/services/api/admin/phong.api'
import { toast } from 'vue3-toastify'
import { Modal } from 'ant-design-vue'

const rooms = ref<any[]>([])
const totalItems = ref(0)
const paginationParams = ref({ page: 1, size: 10 })

const fetchRooms = async () => {
  try {
    const res = await getRooms({
      page: paginationParams.value.page,
      size: paginationParams.value.size,
      sort: 'maPhong,asc'
    })

    rooms.value = res.data.data || []
    totalItems.value = res.data.totalElements || 0

  } catch (error) {
    toast.error('Không thể tải danh sách phòng')
    console.error(error)
  }
}




const handlePageChange = (params: { page: number; pageSize: number }) => {
  paginationParams.value.page = params.page
  paginationParams.value.size = params.pageSize
  fetchRooms()
}
const handleDeleteClick = async (id: string) => {
  Modal.confirm({
    title: 'Xác nhận xóa phòng',
    content: 'Bạn có chắc chắn muốn xóa phòng này không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        const res = await deleteRoom(id); // local variable
        toast.success(res.message);
        fetchRooms(); 
      } catch (error: any) {
        toast.error(error?.response?.data?.message || 'Xóa phòng thất bại');
      }
    }
  });
};

onMounted(() => {
  fetchRooms()
})
</script>
