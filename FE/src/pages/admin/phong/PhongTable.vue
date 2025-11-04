<template>
  <DivCustom label="Danh sách phòng" customClasses="mt-5">
    <!-- Thanh trên: chứa nút lịch sử -->
    <div
      style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <div></div> <!-- div trống ép nút sang phải -->
      <div>
        <a-button type="primary" @click="showHistory = true"
                  class="d-flex justify-content-center items-center px-4">
          <template #icon>
            <i class="ri-history-line"></i>
          </template>
          Lịch sử
        </a-button>
      </div>
    </div>

    <!-- Bảng danh sách phòng -->
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
        :scroll="{ y: 800 }"
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
        </template>
      </a-table>
    </div>

    <!-- Modal hiển thị lịch sử -->
    <a-modal
      v-model:open="showHistory"
      title="Lịch sử sử dụng phòng"
      :width="950"
      centered
      :footer="null"
    >
      <!-- Bộ lọc -->
      <div class="flex items-center mb-4" style="column-gap: 16px;">
        <a-input
          v-model:value="filterParams.tenKhachHang"
          placeholder="Tên khách hàng"
          allow-clear
          style="width: 350px"
        />

        <a-date-picker
          v-model:value="filterParams.tuNgay"
          placeholder="Từ ngày"
          format="DD/MM/YYYY"
          style="width: 220px"
        />

        <a-date-picker
          v-model:value="filterParams.denNgay"
          placeholder="Đến ngày"
          format="DD/MM/YYYY"
          style="width: 220px"
        />

        <a-button
          type="primary"
          @click="fetchRoomHistory"
          class="flex items-center justify-center"
          style="height: 32px"
        >
          <template #icon>
            <i class="ri-search-line"></i>
          </template>
          Tìm kiếm
        </a-button>
      </div>


      <!-- Bảng lịch sử -->
      <a-table
        :columns="columnsHistory"
        :data-source="roomHistory"
        :loading="loadingHistory"
        :pagination="false"
        size="small"
        bordered
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tongTienPhatSinh'">
            {{ formatCurrency(record.tongTienPhatSinh) }}
          </template>
          <template v-if="column.key === 'trangThaiPhong'">
            <a-tag :color="getRoomStatusColor(record.trangThaiPhong)">
              {{ getRoomStatusText(record.trangThaiPhong) }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </DivCustom>
</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustomTable.vue'
import type {TableColumnsType} from 'ant-design-vue'
import {ref, reactive, watch} from 'vue'
import {getAllRoomUsageHistory} from '@/services/api/admin/phong.api.ts'
import type {LeTanResponse, LsDatPhongRequest} from '@/services/api/admin/phong.api.ts'
import dayjs from 'dayjs'

defineProps<{
  rooms: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const emit = defineEmits(['page-change'])

/* ----------------- Cột bảng phòng ----------------- */
const columns: TableColumnsType = [
  {title: 'STT', key: 'stt', dataIndex: 'stt', width: 80, align: 'center'},
  {title: 'Mã phòng', key: 'maPhong', dataIndex: 'maPhong', width: 120, align: 'center'},
  {title: 'Tên phòng', key: 'tenPhong', dataIndex: 'tenPhong', width: 150, align: 'center'},
  {title: 'Loại phòng', key: 'loaiPhong', dataIndex: 'loaiPhong', width: 120, align: 'center'},
  {title: 'Giá hiện tại', key: 'giaHienTai', dataIndex: 'giaHienTai', width: 120, align: 'center'},
  {title: 'Sức chứa', key: 'sucChua', dataIndex: 'sucChua', width: 100, align: 'center'},
  {
    title: 'Trạng thái',
    key: 'trangThaiPhong',
    dataIndex: 'trangThaiPhong',
    width: 130,
    align: 'center'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', {page: pagination.current, pageSize: pagination.pageSize})
}

/* ----------------- Helpers ----------------- */
const formatCurrency = (value: number) =>
  new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(value)

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

const showHistory = ref(false)
const roomHistory = ref<LeTanResponse[]>([])
const loadingHistory = ref(false)

/* Bộ lọc trong modal */
const filterParams = reactive<LsDatPhongRequest>({
  tenKhachHang: '',
  tuNgay: null,
  denNgay: null
})

/* Cột của bảng lịch sử */
const columnsHistory: TableColumnsType = [
  {title: 'Khách hàng', dataIndex: 'hoTen', key: 'hoTen', align: 'center'},
  {title: 'Mã phòng', dataIndex: 'maPhong', key: 'maPhong', align: 'center'},
  {title: 'Tên phòng', dataIndex: 'tenPhong', key: 'tenPhong', align: 'center'},
  {title: 'Thời gian đặt', dataIndex: 'thoiGianDat', key: 'thoiGianDat', align: 'center'},
  {title: 'Giá hiện tại', dataIndex: 'giaHienTai', key: 'giaHienTai', align: 'center'},
  {title: 'Trạng thái', dataIndex: 'trangThaiPhong', key: 'trangThaiPhong', align: 'center'},
  {
    title: 'Tổng tiền phát sinh',
    dataIndex: 'tongTienPhatSinh',
    key: 'tongTienPhatSinh',
    align: 'center'
  }
]

/* Hàm gọi API lịch sử */
const fetchRoomHistory = async () => {
  try {
    loadingHistory.value = true

    const params: LsDatPhongRequest = {
      tenKhachHang: filterParams.tenKhachHang || undefined,
      tuNgay: filterParams.tuNgay ? dayjs(filterParams.tuNgay).format('YYYY-MM-DD') : null,
      denNgay: filterParams.denNgay ? dayjs(filterParams.denNgay).format('YYYY-MM-DD') : null
    }

    const res = await getAllRoomUsageHistory(params)
    console.log('📦 Lịch sử phòng response:', res)

    const data =
      res?.data?.data?.data ||
      res?.data?.data ||
      res?.data ||
      []

    roomHistory.value = Array.isArray(data) ? data : []
    console.log('✅ Dữ liệu hiển thị:', roomHistory.value)

  } catch (err) {
    console.error('❌ Lỗi khi load lịch sử phòng:', err)
    roomHistory.value = []
  } finally {
    loadingHistory.value = false
  }
}

/* Tự load dữ liệu khi mở modal */
watch(showHistory, (val) => {
  if (val) fetchRoomHistory()
})
</script>

<style scoped>
:deep(.ant-table) {
  font-size: 13px;
}
</style>
