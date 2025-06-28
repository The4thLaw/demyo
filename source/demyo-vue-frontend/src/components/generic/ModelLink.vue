<template>
	<span>
		<template v-if="isArray">
			<span v-for="(item, index) in model" :key="item.id">
				<router-link
					:to="{ name: view, params: { id: item.id } }" :class="`c-ModelLink ${cssClass}`"
				>
					<template v-if="hasDefaultSlot">
						<slot :item="item" /><!--
					--></template>
					<template v-else-if="label">{{ label }}</template>
					<template v-else>{{ item.identifyingName }}</template><!--
				--></router-link><!--
				--><template v-if="commaSeparated && index + 1 < length">, </template>
			</span>
		</template>
		<template v-if="!isArray">
			<router-link :to="{ name: view, params: { id: model.id } }" :class="`c-ModelLink ${cssClass}`">
				<template v-if="hasDefaultSlot">
					<slot :item="model" />
				</template>
				<template v-else-if="label">{{ label }}</template>
				<template v-else>{{ model.identifyingName }}</template>
			</router-link>
		</template>
	</span>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
	model: IModel | IModel[]
	view: string
	label?: string
	commaSeparated?: boolean
	cssClass?: string
}>(), {
	label: undefined,
	commaSeparated: true,
	cssClass: ''
})

const isArray = computed(() => Array.isArray(props.model))
const length = computed(() => Array.isArray(props.model) ? (props.model as IModel[]).length : 1)

const slots = useSlots()
const hasDefaultSlot = computed(() => !!slots.default)
</script>

<style lang="scss">
a.c-ModelLink:not(:hover) {
	color: inherit;
}
</style>
