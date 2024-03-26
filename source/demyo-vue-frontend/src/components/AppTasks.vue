<template>
	<Teleport to="#teleport-appTasks">
		<v-menu v-model="inputVal">
			<template #activator="{ props }">
				<v-btn variant="text" icon="mdi-dots-vertical" v-bind="props" />
			</template>
			<v-list class="c-AppTasks__list">
				<slot />
			</v-list>
		</v-menu>
	</Teleport>
</template>

<script>
export default {
	name: 'AppTasks',

	props: {
		modelValue: {
			type: Boolean,
			default: false
		}
	},

	data() {
		return {
			inputVal: this.modelValue
		}
	},

	watch: {
		modelValue(val) {
			this.inputVal = val
		},

		inputVal(val) {
			this.$emit('update:modelValue', val)
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
