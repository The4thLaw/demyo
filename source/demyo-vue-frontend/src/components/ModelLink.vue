<template>
	<span>
		<template v-if="isArray">
			<router-link
				v-for="item in model" :key="item.id"
				:to="{ name: view, params: { id: item.id }}" class="c-ModelLink c-ModelLink__multi"
			>
				{{ item.identifyingName }}</router-link>
		</template>
		<template v-if="!isArray">
			<router-link :to="{ name: view, params: { id: model.id }}" class="c-ModelLink">
				{{ model.identifyingName }}
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
		}
	},

	computed: {
		isArray() {
			return Array.isArray(this.model)
		}
	}
}
</script>

<style lang="less">
a.c-ModelLink:not(:hover) {
	color: inherit;
}

.c-ModelLink__multi::after {
	content: ', ';
}

.c-ModelLink__multi:last-child::after {
	content: none;
}
</style>
