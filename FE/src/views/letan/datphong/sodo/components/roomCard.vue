<script setup lang="ts">
import { computed } from 'vue'
import { useMessage } from 'naive-ui'
import type { SoDoPhongResponse } from '@/service/api/letan/sodophong'

const props = defineProps<{ room: SoDoPhongResponse & { cleanStatus?: 'clean' | 'notClean' } }>()
const emit = defineEmits(['click', 'updateCleanStatus'])
const message = useMessage()

const menuOptions = [{ label: 'Chuyển trạng thái dọn phòng', key: 'toggleClean' }]

const handleMenuSelect = (key: string) => {
  if (key === 'toggleClean') {
    const newStatus = props.room.cleanStatus === 'clean' ? 'notClean' : 'clean'
    emit('updateCleanStatus', newStatus)
    message.success(`Phòng ${props.room.ma} → ${newStatus === 'clean' ? 'Đã dọn' : 'Chưa dọn'}`)
  }
}

const bgColor = computed(() => {
  switch (props.room.trangThaiPhong) {
    case 'TRONG': return 'bg-white border-gray-200 text-gray-900'
    case 'SAP_NHAN': return 'bg-blue-100 border-blue-200 text-gray-900'
    case 'DANG_SU_DUNG': return 'bg-green-100 border-green-200 text-gray-900'
    case 'SAP_TRA': return 'bg-yellow-100 border-yellow-200 text-gray-900'
    case 'QUA_GIO_TRA': return 'bg-red-100 border-red-200 text-gray-900'
    default: return 'bg-gray-100 border-gray-200 text-gray-900'
  }
})

const cleanTagStyle = computed(() => ({
  backgroundColor: props.room.cleanStatus === 'clean' ? '#e8f5e9' : '#fdecea',
  color: props.room.cleanStatus === 'clean' ? '#2e7d32' : '#c62828',
  fontWeight: '600',
  padding: '0.15rem 0.4rem',
  fontSize: '0.75rem',
  display: 'flex',
  alignItems: 'center',
  gap: '0.25rem'
}))

const formatPrice = (price: number | null) =>
  price ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price) : '-'
</script>

<template> 
<n-card class="room-card flex flex-col justify-between p-3 rounded-xl cursor-pointer transition-shadow border" :class="bgColor" @click="$emit('click', room)" size="small" style="min-width: 142px; margin-top: 0px;" > 
    <!-- Header: Trạng thái dọn phòng --> 
     <div class="flex justify-between items-center mb-2"> 
        <n-tag size="small" round :style="cleanTagStyle"> 
            <n-icon-wrapper :size="16" :color="cleanTagStyle.color" :border-radius="999"> 
                <nova-icon :size="14" :icon="room.cleanStatus === 'clean' ? 'carbon:magic-wand' : 'icon-park-outline:cosmetic-brush'" /> 
            </n-icon-wrapper> {{ room.cleanStatus === 'clean' ? 'Sạch' : 'Chưa dọn' }} 
            </n-tag> 
            <n-dropdown :options="menuOptions" trigger="click" @select="handleMenuSelect"> 
                <n-button quaternary circle size="tiny"> 
                    <n-icon-wrapper :size="20"> 
                        <nova-icon icon="icon-park-outline:more-one" color="#ffffff"/> 
                    </n-icon-wrapper> 
                </n-button> 
            </n-dropdown> 
            </div> <!-- Thông tin phòng --> 
            <div class="flex flex-col gap-1 mb-2 truncate"> 
                <div class="text-base font-semibold text-gray-800 truncate">{{ room.ma }}</div> 
                <div class="text-sm text-gray-600" v-if="room.status === 'empty'">
                     {{ formatPrice(room.price) }}/đêm </div> </div> <!-- Trạng thái stayDays --> 
                     <div class="flex justify-end mt-auto"> <n-tag v-if="room.status !== 'empty'" size="small" round style="background-color: rgba(255,255,255,0.4); color:#333; font-weight:500; font-size:0.75rem;" >
                         {{ room.stayDays || 0 }} / {{ room.totalDays || 0 }} ngày </n-tag> </div> </n-card> </template>

                         <style scoped> .room-card { min-height: 160px; transition: all 0.2s ease; } .room-card:hover { border: 1px solid #000000; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); } </style>