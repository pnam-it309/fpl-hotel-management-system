<template>
    <n-modal v-model:show="show" preset="card" :title="title" :style="{ width: '720px' }">
        <n-form label-placement="left" label-width="140">
            <n-grid cols="2" :x-gap="16" :y-gap="8">
                <n-gi>
                    <n-form-item label="Mã nhân viên">
                        <n-input v-model:value="model.code" placeholder="VD: NV001" />
                    </n-form-item>
                </n-gi>
                <n-gi>
                    <n-form-item label="Họ và tên">
                        <n-input v-model:value="model.ten" placeholder="Nhập họ và tên" />
                    </n-form-item>
                </n-gi>
                <n-gi>
                    <n-form-item label="Giới tính">
                        <n-select v-model:value="model.gioiTinh" :options="genderOptions" placeholder="Chọn giới tính" />
                    </n-form-item>
                </n-gi>
                <n-gi>
                    <n-form-item label="Vai trò">
                        <n-select v-model:value="model.vaiTro" :options="roleOptions" placeholder="Chọn vai trò" />
                    </n-form-item>
                </n-gi>
                <n-gi>
                    <n-form-item label="Số điện thoại">
                        <n-input v-model:value="model.sdt" placeholder="Nhập số điện thoại" />
                    </n-form-item>
                </n-gi>
                <n-gi>
                    <n-form-item label="Email">
                        <n-input v-model:value="model.email" placeholder="Nhập email" />
                    </n-form-item>
                </n-gi>
                <n-gi>
                    <n-form-item label="CCCD">
                        <n-input v-model:value="model.cccd" placeholder="Nhập CCCD" />
                    </n-form-item>
                </n-gi>
                <!-- <n-gi>
                    <n-form-item label="Tên đăng nhập">
                        <n-input v-model:value="model.user" placeholder="Tên đăng nhập" />
                    </n-form-item>
                </n-gi> -->
            </n-grid>
        </n-form>

        <template #footer>
            <n-space justify="end">
                <n-button quaternary @click="onClose">Đóng</n-button>
                <n-button type="primary" :loading="saving" :disabled="!canSave" @click="onSave">Lưu</n-button>
            </n-space>
        </template>
    </n-modal>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { NButton, NForm, NFormItem, NGi, NGrid, NInput, NModal, NSelect, NSpace } from 'naive-ui'
import { modifyNhanVien, getNhanVienById } from '@/service/api/nhansu/nhanvien'

const props = defineProps<{ show: boolean; editId?: string | null }>()
const emit = defineEmits<{ (e: 'update:show', v: boolean): void; (e: 'refresh'): void }>()

const show = computed({
    get: () => props.show,
    set: (v: boolean) => emit('update:show', v),
})

const genderOptions = [
    { label: 'Nam', value: 'male' },
    { label: 'Nữ', value: 'female' },
]

const roleOptions = [
    { label: 'Quản lí', value: 'ADMIN' },
    { label: 'Lễ Tân', value: 'STAFF' },
]

const model = reactive({
    id: '' as string | '',
    code: '',
    ten: '',
    gioiTinh: null as 'male' | 'female' | null,
    sdt: '',
    email: '',
    cccd: '',
    user: '',
    vaiTro: '' as string,
})

// Lưu lại vai trò ban đầu để tránh ghi đè nếu người dùng không đổi
const initialVaiTro = ref<string>('')

const saving = ref(false)
const canSave = computed(() => !!model.ten && !!model.email && !!model.sdt && model.gioiTinh !== null && !!model.vaiTro)

const title = computed(() => (props.editId ? 'Sửa nhân viên' : 'Thêm nhân viên'))

function onClose() {
    show.value = false
}

async function onSave() {
    if (!canSave.value) return
    saving.value = true
    try {
        const fd = new FormData()
        if (model.id) fd.append('id', model.id)
        if (model.code) fd.append('code', model.code)
        fd.append('ten', model.ten)
        if (model.email) fd.append('email', model.email)
        if (model.sdt) fd.append('sdt', model.sdt)
        if (model.cccd) fd.append('cccd', model.cccd)
        if (model.user) fd.append('user', model.user)
        if (model.gioiTinh !== null) fd.append('gioiTinh', String(model.gioiTinh === 'male'))
        // Chỉ gửi vaiTro khi người dùng thực sự thay đổi
        if (model.vaiTro && model.vaiTro !== initialVaiTro.value) {
            fd.append('vaiTro', model.vaiTro)
        }

        await modifyNhanVien(fd)
        ;(window as any).$message?.success?.('Lưu nhân viên thành công')
        emit('refresh')
        show.value = false
    } catch (e: any) {
        ;(window as any).$message?.error?.(e?.message || 'Lưu nhân viên thất bại')
    } finally {
        saving.value = false
    }
}

function resetModel() {
    model.id = ''
    model.code = ''
    model.ten = ''
    model.gioiTinh = null
    model.sdt = ''
    model.email = ''
    model.cccd = ''
    model.user = ''
    model.vaiTro = ''
}

watch(
    () => props.show,
    async (v) => {
        if (!v) return
        if (props.editId) {
            try {
                const data: any = await getNhanVienById(props.editId)
                model.id = data.id || ''
                model.code = data.ma || ''
                model.ten = data.ten || ''
                model.sdt = data.sdt || ''
                model.email = data.email || ''
                model.cccd = data.cccd || ''
                model.user = data.user || ''
                // vaiTro: ưu tiên chucVu từ BE; map 0/1 hoặc các chuỗi cũ sang ADMIN/STAFF
                const cv = (data.chucVu !== undefined ? data.chucVu : data.vaiTro)
                if (typeof cv === 'number') {
                    model.vaiTro = cv === 1 ? 'ADMIN' : 'STAFF'
                } else if (typeof cv === 'string') {
                    const up = cv.toUpperCase()
                    if (up === 'ADMIN' || up === 'STAFF') model.vaiTro = up
                    else if (up === 'MANAGER' || up === 'QUAN_LY') model.vaiTro = 'ADMIN'
                    else if (up === 'RECEPTIONIST' || up === 'LE_TAN') model.vaiTro = 'STAFF'
                    else model.vaiTro = ''
                } else {
                    model.vaiTro = ''
                }
                initialVaiTro.value = model.vaiTro

                // gioiTinh: BE field có thể là 'gioiTimh' (entity setter), hoặc 'gioiTinh'
                const gsrc = (data.gioiTimh !== undefined ? data.gioiTimh : data.gioiTinh)
                if (gsrc === true || gsrc === 'male' || gsrc === 1) {
                    model.gioiTinh = 'male'
                } else if (gsrc === false || gsrc === 'female' || gsrc === 0) {
                    model.gioiTinh = 'female'
                } else {
                    model.gioiTinh = null
                }
            } catch (e: any) {
                ;(window as any).$message?.error?.('Không tải được dữ liệu nhân viên')
                resetModel()
            }
        } else {
            resetModel()
        }
    },
    { immediate: false }
)
</script>

<style scoped></style>