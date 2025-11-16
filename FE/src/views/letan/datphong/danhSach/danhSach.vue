<template>
  <div>
    <h3>Sơ đồ phòng</h3>
    <div class="grid grid-cols-4 gap-2">
      <div v-for="room in rooms" :key="room.id" class="border p-2 text-center">
        {{ room.name }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      rooms: [
        { id: 1, name: 'Phòng 101' },
        { id: 2, name: 'Phòng 102' },
        { id: 3, name: 'Phòng 103' },
        { id: 4, name: 'Phòng 104' },
      ]
    }
  }
}
</script>
<template>
  <div class="p-8 space-y-8 bg-gray-50 min-h-screen">

    <!-- Nút lọc trạng thái phòng -->
    <div class="flex space-x-4 mb-4">
      <n-button
        v-for="status in statuses"
        :key="status.key"
        :style="{ 
          backgroundColor: selectedStatuses.includes(status.key) ? status.color : 'white', 
          color: selectedStatuses.includes(status.key) ? 'white' : 'black',
          fontWeight: '600'
        }"
        @click="toggleStatus(status.key)"
      >
        {{ status.label }} ({{ countRooms(status.key) }})
      </n-button>
    </div>

    <!-- Danh sách tầng -->
    <FloorRow
      v-for="floor in filteredFloors"
      :key="floor.floor"
      :floor="floor.floor"
      :rooms="floor.rooms"
      @room-click="handleRoomClick"
      @updateCleanStatus="updateRoomCleanStatus"
    />
  </div>
</template>
