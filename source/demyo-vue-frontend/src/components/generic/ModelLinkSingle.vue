<template>
	<router-link
		v-if="view !== 'AuthorPseudonym'"
		:to="{ name: view, params: { id: model.id } }" :class="`c-ModelLink ${cssClass}`"
	>
		<template v-if="hasDefaultSlot">
			<slot :item="model" />
		</template>
		<template v-else-if="label">{{ label }}</template>
		<template v-else>{{ model.identifyingName }}</template>
	</router-link>
	<router-link
		v-else-if="!model.pseudonymOf?.id"
		:to="{ name: 'AuthorView', params: { id: model.id } }" :class="`c-ModelLink ${cssClass}`"
	>
		{{ model.identifyingName }}
	</router-link>
	<span v-else>
		<router-link
			:to="{ name: 'AuthorView', params: { id: model.pseudonymOf.id } }"
			:class="`c-ModelLink ${cssClass}`"
		>
			{{ model.pseudonymOf.identifyingName }}
		</router-link>
		{{ $t('field.Author.asPseudonym.before') }}
		<router-link
			:to="{ name: 'AuthorView', params: { id: model.id } }"
			:class="`c-ModelLink ${cssClass}`"
		>
			{{ model.identifyingName }}
		</router-link><!--
		-->{{ $t('field.Author.asPseudonym.after') }}
	</span>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
	model: IModel
	view: string
	label?: string
	cssClass?: string
}>(), {
	label: undefined,
	cssClass: ''
})

const slots = useSlots()
const hasDefaultSlot = computed(() => !!slots.default)
</script>

<style lang="scss">
a.c-ModelLink:not(:hover) {
	color: inherit;
}
</style>
