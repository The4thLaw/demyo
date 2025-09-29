<template>
	<span>
		<template v-if="isArray">
			<span v-for="(item, index) in (model as IModel[])" :key="item.id">
				<ModelLinkSingle
					:model="item" :view="view" :label="label" :css-class="cssClass"
				>
					<template v-if="$slots.default" #default>
						<slot name="default" :item="item" />
					</template>
				</ModelLinkSingle>
				<template v-if="commaSeparated && index + 1 < length">, </template>
			</span>
		</template>
		<template v-if="!isArray">
			<ModelLinkSingle
				:model="model" :view="view" :label="label" :css-class="cssClass"
			>
				<template v-if="$slots.default" #default>
					<slot name="default" :item="model" />
				</template>
			</ModelLinkSingle>
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
</script>

<style lang="scss">
a.c-ModelLink:not(:hover) {
	color: inherit;
}
</style>
