<template>
  <NSpace vertical size="large">
    <n-card>
      <n-space vertical :size="8">
        <n-input
          v-model:value="state.keyword"
          placeholder="Tìm theo tên, mã, hoặc số điện thoại..."
          clearable
        />

        <n-grid cols="2 s:2 m:2 l:2 xl:2" :x-gap="8" :y-gap="8">
          <n-gi>
            <n-select
              v-model:value="state.gender"
              :options="genderOptions"
              placeholder="Giới tính"
              clearable
            />
          </n-gi>
          <n-gi>
            <n-select
              v-model:value="state.role"
              :options="roleOptions"
              placeholder="Vai trò"
              clearable
            />
          </n-gi>
          
        </n-grid>

        <div class="flex justify-end">
          <n-button strong secondary @click="handleReset">Reset</n-button>
        </div>
      </n-space>
    </n-card>

    <n-card>
      <div class="flex gap-4 mb-3">
        <n-button type="primary" @click="onAdd">Thêm nhân viên</n-button>
      </div>
      <div class="mb-3 flex items-center gap-2">
        <span class="text-gray"></span>
        <VueDraggable v-model="state.middleKeys" item-key="key" class="flex gap-2">
          <template #item="{ element }">
            <n-tag type="default" :bordered="false">{{ columnMap[element].title }}</n-tag>
          </template>
        </VueDraggable>
      </div>

      <n-data-table
        :columns="columns"
        :data="rows"
        :row-key="(row:any)=>row.id"
        :bordered="true"
        :single-line="false"
        size="small"
        :scroll-x="1000"
        :loading="loading"
      />

      <div class="mt-4 flex justify-end">
        <n-pagination
          v-model:page="currentPage"
          :page-count="Math.ceil(totalItems / pageSize) || 1"
          :page-size="pageSize"
          size="small"
          :page-slot="7"
          prev-label="Trước"
          next-label="Sau"
          show-size-picker
          :page-sizes="[10, 20, 30, 50]"
          @update:page="changePage"
          @update:page-size="(s:number)=>{ pageSize=s; fetchList(1) }"
        >
          <template #prefix>
            Tổng {{ totalItems }} nhân viên
          </template>
        </n-pagination>
      </div>
    </n-card>

    <Tablemodal v-model:show="state.showModal" :edit-id="state.editId" @refresh="fetchList(currentPage)" />
  </NSpace>
</template>

<script setup lang="ts">
import { reactive, computed, ref, watch, onMounted, h } from 'vue'
import { NButton, NCard, NDataTable, NGrid, NGi, NInput, NPagination, NSelect, NSpace, NTag } from 'naive-ui'
import { VueDraggable } from 'vue-draggable-plus'
import Tablemodal from './components/TableModal.vue'
import { getAllNhanVien, changeNhanVienRole } from '@/service/api/nhansu/nhanvien'

// Định nghĩa cột cố định và map cột có thể kéo trước để dùng type
const expandColumn = {
  type: 'expand' as const,
  expandable: () => true,
  renderExpand: (row: any) => {
    return h(
      'div',
      { style: 'background:#eef4ff;padding:16px;border-radius:8px' },
      [
        h('div', { style: 'display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:12px' }, [
          h('div', [h('strong', 'Mã:'), ' ', row.code || '-']),
          h('div', [h('strong', 'Họ tên:'), ' ', row.employee || '-']),
          h('div', [h('strong', 'Giới tính:'), ' ', row._gender || '-']),
          h('div', [h('strong', 'Vai trò:'), ' ', row.role || '-']),
          h('div', [h('strong', 'SĐT:'), ' ', row._sdt || '-']),
          h('div', [h('strong', 'Email:'), ' ', row._email || '-']),
          h('div', [h('strong', 'CCCD:'), ' ', row._cccd || '-']),
          h('div', [h('strong', 'Trạng thái:'), ' ', row.status || '-']),
          //h('div', [h('strong', 'Tên đăng nhập:'), ' ', row._user || '-']),
        ]),
      ]
    )
  },
}
const codeColumn = { title: 'Mã', key: 'code', width: 120, fixed: 'left' as const }
const actionsColumn = {
  title: 'Thao tác',
  key: 'actions',
  width: 160,
  fixed: 'right' as const,
  render: (row: any) => {
    return [
      h(
        NButton,
        {
          size: 'small',
          type: 'success',
          onClick: () => onEdit(row.id),
        },
        { default: () => 'Sửa' }
      ),
      h(
        NButton,
        {
          size: 'small',
          type: 'error',
          style: 'margin-left:8px',
          onClick: () => onToggleRole(row.id),
        },
        { default: () => 'Thay đổi' }
      ),
    ]
  },
}

const columnMap: Record<string, any> = {
  employee: { title: 'Nhân viên', key: 'employee', width: 220 },
  contact: { title: 'Liên hệ', key: 'contact', width: 240 },
  role: { title: 'Vai trò', key: 'role', width: 140 },
  status: { title: 'Trạng thái', key: 'status', width: 140 },
}

type ColumnKeys = keyof typeof columnMap

const state = reactive({
  keyword: '',
  gender: null as string | null,
  role: null as string | null,
  showModal: false,
  editId: null as string | null,
  // danh sách các key cột có thể kéo thả (giữ nguyên expand, mã đầu và thao tác cuối)
  middleKeys: ['employee', 'contact', 'role', 'status'] as ColumnKeys[],
})

const loading = ref(false)
const listData = ref<any[]>([])
const totalItems = ref(0)
const currentPage = ref(1)
let pageSize = ref(10)

const genderOptions = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Nam', value: 'male' },
  { label: 'Nữ', value: 'female' },
]

const roleOptions = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Lễ tân', value: 'receptionist' },
  { label: 'Quản lý', value: 'manager' },
]

const columns = computed(() => {
  return [expandColumn, codeColumn, ...state.middleKeys.map(k => columnMap[k as string]), actionsColumn]
})

function onAdd() {
  state.editId = null
  state.showModal = true
}

function onEdit(id: string) {
  state.editId = id
  state.showModal = true
}

function handleReset() {
  state.keyword = ''
  state.gender = null
  state.role = null
}

const rows = computed(() =>
  listData.value.map((it: any) => ({
    id: it.id,
    code: it.ma,
    employee: it.ten,
    contact: [it.sdt, it.email].filter(Boolean).join(' • '),
    // Vai trò: ưu tiên chucVu (ADMIN/STAFF) từ BE; fallback các định dạng cũ
    role: (() => {
      const cv = (it.chucVu ?? it.vaiTro)
      if (typeof cv === 'string') {
        const up = cv.toUpperCase()
        if (up === 'ADMIN' || up === 'QUAN_LY' || up === 'MANAGER') return 'Quản lý'
        if (up === 'STAFF' || up === 'LE_TAN' || up === 'RECEPTIONIST') return 'Lễ tân'
        return cv
      }
      if (typeof cv === 'number') return cv === 0 ? 'Quản lý' : 'Lễ tân'
      return '-'
    })(),
    // Trạng thái: map enum ACTIVE/INACTIVE, hoặc số/boolean
    status: (() => {
      const st = it.status ?? it.trangThai ?? it.hoatDong
      if (typeof st === 'string') {
        const up = st.toUpperCase()
        if (up === 'ACTIVE') return 'Hoạt động'
        if (up === 'INACTIVE') return 'Ngưng hoạt động'
        return st
      }
      if (typeof st === 'number') return st === 1 ? 'Hoạt động' : 'Ngưng hoạt động'
      if (typeof st === 'boolean') return st ? 'Hoạt động' : 'Ngưng hoạt động'
      return '-'
    })(),
    // Dữ liệu chi tiết cho panel mở rộng
    _gender: (() => {
      const g = (it.gioiTinh !== undefined ? it.gioiTinh : it.gioiTimh)
      if (typeof g === 'boolean') return g ? 'Nam' : 'Nữ'
      if (typeof g === 'number') return g === 1 ? 'Nam' : g === 0 ? 'Nữ' : '-'
      if (typeof g === 'string') {
        const up = g.toUpperCase()
        if (up === 'MALE' || up === 'NAM') return 'Nam'
        if (up === 'FEMALE' || up === 'NU' || up === 'NỮ') return 'Nữ'
        return '-'
      }
      return '-'
    })(),
    _sdt: it.sdt || '',
    _email: it.email || '',
    _cccd: it.cccd || '-',
    _user: it.user || '-',
    actions: '',
  }))
)

async function fetchList(page = 1) {
  loading.value = true
  try {
    const res = await getAllNhanVien({ page, size: pageSize.value, q: state.keyword || undefined })
    listData.value = res.items
    totalItems.value = res.totalItems
    currentPage.value = res.currentPage
  } catch (e:any) {
    listData.value = []
    totalItems.value = 0
  } finally {
    loading.value = false
  }
}

function changePage(page:number){
  fetchList(page)
}

async function onToggleRole(id: string) {
  try {
    await changeNhanVienRole(id)
    ;(window as any).$message?.success?.('Đã đổi vai trò')
    fetchList(currentPage.value)
  } catch (e:any) {
    ;(window as any).$message?.error?.(e?.message || 'Thao tác thất bại')
  }
}

watch(() => state.keyword, () => fetchList(1))
onMounted(() => fetchList(1))
</script>

<style scoped>
</style>
