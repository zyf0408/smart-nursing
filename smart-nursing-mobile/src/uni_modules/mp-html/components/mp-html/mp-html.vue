<template>
  <!--
    mp-html 富文本渲染组件（简化包装版）
    
    生产环境建议安装官方 mp-html 插件:
      npm install mp-html
    或通过 uni_modules 市场导入完整版 mp-html 组件。
    
    本组件作为占位包装，使用 uni-app 内置 rich-text 组件进行基础渲染。
    如需完整功能（图片预览、视频播放、代码高亮、表格等），请替换为官方 mp-html。
  -->
  <view class="mp-html-container">
    <rich-text
      :id="id"
      class="mp-html-content"
      :nodes="processedNodes"
      :selectable="selectable"
      :image-menu-prevent="!previewImage"
      @itemclick="onItemClick"
    ></rich-text>
  </view>
</template>

<script>
/**
 * mp-html 组件 - 简化包装版
 * 
 * Props:
 * - content: HTML 字符串内容
 * - selectable: 是否允许长按复制内容
 * - previewImage: 是否允许图片预览点击
 * - tagStyle: 标签样式对象（本简化版仅做基本处理）
 * 
 * Events:
 * - linktap: 链接点击事件
 * - imgtap: 图片点击事件
 * 
 * 注意: rich-text 组件不支持事件冒泡和交互，
 * 如需 linktap/imgtap 事件，请使用官方 mp-html 插件。
 */

export default {
  name: 'mp-html',
  props: {
    // HTML 内容字符串
    content: {
      type: String,
      default: ''
    },
    // 是否可选
    selectable: {
      type: Boolean,
      default: false
    },
    // 是否允许图片预览
    previewImage: {
      type: Boolean,
      default: true
    },
    // 标签样式（简化版暂不支持，保留接口兼容）
    tagStyle: {
      type: Object,
      default() {
        return {}
      }
    },
    // 组件 ID
    id: {
      type: String,
      default: 'mp-html'
    }
  },
  emits: ['linktap', 'imgtap', 'load', 'error'],
  computed: {
    /**
     * 处理 HTML 节点
     * 将 content 字符串转换为 rich-text 可渲染的 nodes
     */
    processedNodes() {
      if (!this.content) return ''
      
      let html = this.content
      
      // 基本样式处理：将 tagStyle 中的样式注入到对应标签
      // 注意: rich-text 的 nodes 格式不支持完整 CSS，
      // 这里仅做简单处理，完整功能请使用官方 mp-html
      if (this.tagStyle && Object.keys(this.tagStyle).length > 0) {
        Object.keys(this.tagStyle).forEach(tag => {
          const style = this.tagStyle[tag]
          // 简单的样式注入（仅做基本支持）
          const regex = new RegExp(`<${tag}(?!\\w)`, 'gi')
          html = html.replace(regex, `<${tag} style="${style}"`)
        })
      }
      
      // 处理图片：添加最大宽度限制
      html = html.replace(/<img/gi, '<img style="max-width:100%;height:auto;border-radius:8rpx;"')
      
      return html
    }
  },
  methods: {
    /**
     * rich-text itemclick 事件
     * 注意: rich-text 的事件支持有限，
     * 官方 mp-html 支持完整的 linktap/imgtap 事件
     */
    onItemClick(e) {
      // rich-text 的 itemclick 事件返回的节点信息有限
      // 完整的链接和图片点击处理需要官方 mp-html
      // console.log('item click:', e)
    },
    
    /**
     * 图片点击预览（简化版不支持，需 mp-html）
     */
    previewImg(src) {
      if (this.previewImage && src) {
        uni.previewImage({
          urls: [src],
          current: src
        })
      }
    }
  },
  watch: {
    content: {
      handler() {
        this.$emit('load')
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
.mp-html-container {
  width: 100%;
  font-size: 30rpx;
  line-height: 1.8;
  color: #333;
  word-wrap: break-word;
  overflow: hidden;
}

.mp-html-content {
  width: 100%;
}
</style>
