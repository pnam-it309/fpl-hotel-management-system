<script setup lang="ts">
import { computed, defineEmits, defineProps, onMounted, ref, watch } from 'vue'
import { addPhong, getPhongById, getRoomTypes, updatePhong } from '@/service/api/letan/phong'

interface Room {
  id?: string
  maPhong: string
  tenPhong: string
  tang?: number
  loaiPhong?: string
  gia?: number
  sucChua?: number
  trangThaiHoatDong: string
}

interface Props {
  visible: boolean
  type?: 'add' | 'edit'
  modalData?: { id: string } | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:visible', visible: boolean): void
  (e: 'refresh'): void
}>()

const modalVisible = computed({
  get: () => props.visible,
  set: (val: boolean) => emit('update:visible', val),
})

function closeModal() {
  modalVisible.value = false
}

const title = computed(() => (props.type === 'edit' ? 'Sửa phòng' : 'Thêm phòng'))

const defaultRoom: Room = {
  maPhong: '',
  tenPhong: '',
  tang: undefined,
  loaiPhong: undefined,
  gia: undefined,
  sucChua: undefined,
  trangThaiHoatDong: 'DANG_HOAT_DONG',
}

const formModel = ref<Room>({ ...defaultRoom })
const isLoading = ref(false)

// --- Loại phòng ---
const loaiPhongOptions = ref<{ label: string; value: string; soLuongNguoiToiDa: number; giaCaNgay: number }[]>([])
const isSucChuaLocked = ref(false)
const isGiaLocked = ref(false)

// --- Trạng thái phòng ---
const trangThaiOptions = ref([
  { label: 'Hoạt động', value: 'DANG_HOAT_DONG' },
  { label: 'Bảo trì', value: 'DANG_SUA' },
  { label: 'Ngưng hoạt động', value: 'NGUNG_HOAT_DONG' },
])

function mapTrangThaiPhong(trangThai: string): string {
  switch (trangThai) {
    case 'Hoạt động':
      return 'DANG_HOAT_DONG'
    case 'Bảo trì':
      return 'DANG_SUA'
    case 'Ngưng hoạt động':
      return 'NGUNG_HOAT_DONG'
    default:
      return 'DANG_HOAT_DONG'
  }
}

function reverseMapTrangThaiPhong(enumValue: string): string {
  switch (enumValue) {
    case 'DANG_HOAT_DONG':
      return 'Hoạt động'
    case 'DANG_SUA':
      return 'Bảo trì'
    case 'NGUNG_HOAT_DONG':
      return 'Ngưng hoạt động'
    default:
      return 'Hoạt động'
  }
}

// --- Fetch loại phòng ---
async function fetchLoaiPhong() {
  try {
    const data = await getRoomTypes()
    loaiPhongOptions.value = data.map(lp => ({
      label: lp.ten,
      value: String(lp.id),
      soLuongNguoiToiDa: lp.soLuongNguoiToiDa ?? 1,
      giaCaNgay: lp.giaCaNgay ?? 0,
    }))
  }
  catch (err: any) {
    window.$message.error(err.message || 'Không thể tải loại phòng')
  }
}

// --- Fetch phòng detail khi edit ---
async function fetchPhongDetail(id: string) {
  try {
    isLoading.value = true
    const data = await getPhongById(id)

    formModel.value = {
      id: data.id,
      maPhong: data.ma,
      tenPhong: data.ten,
      tang: data.tang,
      loaiPhong: data.idLoaiPhong,
      gia: data.giaCaNgay,
      sucChua: data.soNguoiToiDa,
      trangThaiHoatDong: data.trangThaiHoatDong,
    }

    isGiaLocked.value = true
    isSucChuaLocked.value = true
  }
  catch (err: any) {
    window.$message.error(err.message || 'Không thể tải thông tin phòng')
  }
  finally {
    isLoading.value = false
  }
}

// --- Watch loại phòng để tự set giá và sức chứa ---
watch(
  () => formModel.value.loaiPhong,
  (newLoaiPhong) => {
    if (!newLoaiPhong) {
      formModel.value.gia = undefined
      formModel.value.sucChua = undefined
      isGiaLocked.value = false
      isSucChuaLocked.value = false
      return
    }
    const selected = loaiPhongOptions.value.find(lp => lp.value === newLoaiPhong)
    if (selected) {
      formModel.value.gia = selected.giaCaNgay
      formModel.value.sucChua = selected.soLuongNguoiToiDa
      isGiaLocked.value = true
      isSucChuaLocked.value = true
    }
  },
)

// --- Watch tên phòng để auto-fill mã phòng ---
watch(
  () => formModel.value.tenPhong,
  (newTen) => {
    formModel.value.maPhong = newTen
  },
)

// --- Watch khi modal mở ---
watch(
  () => props.visible,
  (val) => {
    if (val) {
      fetchLoaiPhong()
      if (props.type === 'edit' && props.modalData?.id) {
        fetchPhongDetail(props.modalData.id)
      }
      else {
        formModel.value = { ...defaultRoom }
        isGiaLocked.value = false
        isSucChuaLocked.value = false
      }
    }
  },
)

function validateForm() {
  if (!formModel.value.tenPhong?.trim()) {
    window.$message.warning('Vui lòng nhập đầy đủ tên phòng!')
    return false
  }
  if (!formModel.value.loaiPhong) {
    window.$message.warning('Vui lòng chọn loại phòng!')
    return false
  }
  if (!formModel.value.sucChua || formModel.value.sucChua <= 0) {
    window.$message.warning('Sức chứa phải lớn hơn 0!')
    return false
  }
  if (!formModel.value.tang || formModel.value.tang <= 0) {
    window.$message.warning('Tầng phải lớn hơn 0!')
    return false
  }
  return true
}

async function handleSubmit() {
  if (!validateForm()) return

  try {
    if (props.type === 'edit' && formModel.value.id) {
      // Update
      const payload = {
        ma: formModel.value.maPhong.trim(),
        ten: formModel.value.tenPhong.trim(),
        idLoaiPhong: formModel.value.loaiPhong!,
        tang: formModel.value.tang!,
        trangThaiPhong: formModel.value.trangThaiHoatDong,
      }

      const res = await updatePhong(formModel.value.id, payload)
      window.$message.success(res?.message || 'Cập nhật phòng thành công!')
    }
    else {
      // Add
      const payload = {
        ma: formModel.value.maPhong.trim(),
        ten: formModel.value.tenPhong.trim(),
        idLoaiPhong: formModel.value.loaiPhong!,
        sucChua: formModel.value.sucChua!,
        tang: formModel.value.tang!,
        trangThaiHoatDong: formModel.value.trangThaiHoatDong,
      }

      const res = await addPhong(payload)

      if (res?.message?.includes('Tên phòng này đã tồn tại')) {
        window.$message.warning('Tên phòng này đã tồn tại!')
        return
      }

      window.$message.success(res?.message || 'Thêm phòng thành công!')
    }

    emit('refresh')
    closeModal()
    formModel.value = { ...defaultRoom }
  }
  catch (error: any) {
    window.$message.error(error.message || (props.type === 'edit' ? 'Không thể cập nhật phòng' : 'Không thể thêm phòng'))
  }
}

onMounted(fetchLoaiPhong)
</script>

<template>
  <n-modal
    v-model:show="modalVisible"
    :mask-closable="false"
    preset="card"
    :title="title"
    class="w-700px"
    :segmented="{ content: true, action: true }"
  >
    <n-spin :show="isLoading">
      <n-form label-placement="left" :model="formModel" label-align="left" :label-width="120">
        <n-grid :cols="24" :x-gap="18">
          <n-form-item-grid-item :span="12" label="Mã phòng" path="maPhong">
            <n-input v-model:value="formModel.maPhong" placeholder="Mã phòng theo tên phòng" disabled />
          </n-form-item-grid-item>

          <n-form-item-grid-item :span="12" label="Tên phòng" path="tenPhong">
            <n-input v-model:value="formModel.tenPhong" placeholder="Nhập tên phòng" />
          </n-form-item-grid-item>

          <n-form-item-grid-item :span="12" label="Tầng" path="tang">
            <n-input-number v-model:value="formModel.tang" :min="1" :max="3" placeholder="Nhập tầng" style="width: 100%" />
          </n-form-item-grid-item>

          <n-form-item-grid-item :span="12" label="Loại phòng" path="loaiPhong">
            <n-select
              v-model:value="formModel.loaiPhong"
              :options="loaiPhongOptions"
              label-field="label"
              value-field="value"
              placeholder="Chọn loại phòng"
            />
          </n-form-item-grid-item>

          <n-form-item-grid-item :span="12" label="Giá (VNĐ)" path="gia">
            <n-input-number
              v-model:value="formModel.gia"
              :min="0"
              placeholder="Tự động từ loại phòng"
              :disabled="isGiaLocked"
              style="width: 100%"
            />
          </n-form-item-grid-item>

          <n-form-item-grid-item :span="12" label="Sức chứa" path="sucChua">
            <n-input-number
              v-model:value="formModel.sucChua"
              :min="1"
              placeholder="Tự động từ loại phòng"
              :disabled="isSucChuaLocked"
              style="width: 100%"
            />
          </n-form-item-grid-item>

          <n-form-item-grid-item :span="12" label="Trạng thái hoạt động" path="trangThaiHoatDong">
            <n-select
              v-model:value="formModel.trangThaiHoatDong"
              :options="trangThaiOptions"
              label-field="label"
              value-field="value"
              placeholder="Chọn trạng thái"
            />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
    </n-spin>

    <template #action>
      <n-space justify="center">
        <n-button @click="closeModal">
          Hủy
        </n-button>
        <n-button type="primary" @click="handleSubmit">
          Lưu
        </n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<style scoped>
.w-700px {
  width: 700px;
}
</style>
