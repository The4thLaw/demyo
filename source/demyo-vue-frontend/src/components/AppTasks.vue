<template>
	<portal to="appTasks">
		<v-menu v-model="inputVal">
			<template #activator="{ props }">
				<v-btn variant="text" icon="mdi-dots-vertical" v-bind="props" />
			</template>
			<v-list class="c-AppTasks__list">
				<slot />
			</v-list>
		</v-menu>
	</portal>
</template>

<script>
export default {
	name: 'AppTasks',

	props: {
		value: {
			type: Boolean,
			default: false
		}
	},

	data() {
		return {
			inputVal: this.value
		}
	},

	watch: {
		value(val) {
			this.inputVal = val
		},

		inputVal(val) {
			this.$emit('input', val)
		}
	}
}
</script>

<style lang="less">
.c-AppTasks__list {
	a,
	a:hover {
		text-decoration: none;
	}
}
</style>
