<template>
  <el-sub-menu
    v-if="hasChildren"
    :index="indexValue"
  >
    <template #title>
      <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
      <span>{{ item.title }}</span>
    </template>
    <menu-tree
      v-for="child in item.children"
      :key="childKey(child)"
      :item="child"
    />
  </el-sub-menu>
  <el-menu-item v-else :index="item.path || ''">
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
import { computed } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const hasChildren = computed(() => {
  return props.item.children && props.item.children.length > 0
})

const indexValue = computed(() => {
  const item = props.item
  // 优先使用 menuId，其次 id，最后 path，确保返回字符串
  const raw = item.menuId ?? item.id ?? item.path ?? ''
  return String(raw)
})

const childKey = (child) => {
  return String(child.menuId ?? child.id ?? child.path ?? Math.random())
}
</script>
