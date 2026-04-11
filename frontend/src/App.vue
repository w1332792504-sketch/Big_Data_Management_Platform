<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}

/* 系统管理下拉菜单 - 与深色顶栏风格一致 */
.el-popper.system-dropdown {
  background-color: #1f1f1f !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
}

.el-popper.system-dropdown .el-menu-item {
  color: #ccc !important;
}

.el-popper.system-dropdown .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.05) !important;
  color: #fff !important;
}

.el-popper.system-dropdown .el-menu-item.is-active {
  color: #409EFF !important;
}
</style>
