<template>
  <el-sub-menu
    v-if="item.children && item.children.length > 0"
    :index="item.path"
  >
    <template #title>
      <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
      <span>{{ item.title }}</span>
    </template>
    <menu-tree
      v-for="child in item.children"
      :key="child.id"
      :item="child"
    />
  </el-sub-menu>
  <el-menu-item v-else :index="item.path">
    <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
    <span>{{ item.title }}</span>
  </el-menu-item>
</template>

<script>
export default {
  name: 'MenuTree'
}
</script>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>
