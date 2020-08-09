<template>
	<div class="c-DetailView">
		<!-- TODO: this component might not be useful after all -->
		<slot name="list" />
		<!--<v-navigation-drawer permanent right absolute clipped>
			<slot name="detail" />
		</v-navigation-drawer>-->
		<portal to="appSidePane">
			<slot name="detail" />
		</portal>
	</div>
</template>

<script>
export default {
	name: 'DetailView'
}
</script>

<style lang="less">
.c-DetailView {
	background: #fee;
}
</style>
