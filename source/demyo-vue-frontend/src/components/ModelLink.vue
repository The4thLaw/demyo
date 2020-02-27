<template>
	<span>
		<template v-if="isArray">
			<span v-for="(item, index) in model" :key="item.id">
				<router-link
					:to="{ name: view, params: { id: item.id }}" :class="`c-ModelLink ${cssClass}`"
				>
					<template v-if="hasDefaultSlot">
						<slot :item="item" /><!--
					--></template>
					<template v-else>
						{{ item.identifyingName }}<!--
					--></template><!--
				--></router-link><!--
				--><template v-if="commaSeparated && index + 1 < length">, </template>
			</span>
		</template>
		<template v-if="!isArray">
			<router-link :to="{ name: view, params: { id: model.id }}" class="c-ModelLink">
				<template v-if="hasDefaultSlot">
					<slot :item="model" />
				</template>
				<template v-else>
					{{ model.identifyingName }}
				</template>
			</router-link>
		</template>
	</span>
</template>

<script>
export default {
	name: 'ModelLink',

	props: {
		model: {
			type: null,
			required: true
		},

		view: {
			type: String,
			required: true
		},

		commaSeparated: {
			type: Boolean,
			default: true
		},

		cssClass: {
			type: String,
			default: ''
		}
	},

	computed: {
		isArray() {
			return Array.isArray(this.model)
		},

		hasDefaultSlot() {
			return !!this.$scopedSlots.default
		},

		length() {
			return this.isArray ? this.model.length : 1
		}
	}
}
</script>

<style lang="less">
a.c-ModelLink:not(:hover) {
	color: inherit;
}
</style>
