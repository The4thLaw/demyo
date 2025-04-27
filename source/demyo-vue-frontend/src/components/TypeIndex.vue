<template>
	<!-- Note: we don't tie the items per page to the configuration because it's not the same presentation
	and it can get quite long. -->
	<v-data-table
		v-if="items.length"
		:items="items" :headers="headers" :hide-default-footer="items.length < 11"
		:page-text="$t('core.components.VDataTable.page')"
		:items-per-page-text="$t('core.components.VDataTable.itemsPerPage')"
	>
		<template #item.identifyingName="{ item }">
			<ModelLink v-if="viewRoute" :model="item" :view="viewRoute" />
			<template v-else>
				{{ item.identifyingName }}
			</template>
		</template>
		<template #item.actions="{ item }">
			<v-btn :to="{ name: editRoute, params: { id: item.id } }" icon variant="text">
				<v-icon>mdi-pencil</v-icon>
			</v-btn>
			<v-btn v-if="deletable" icon variant="text" @click="$emit('delete', item.id)">
				<v-icon>mdi-delete</v-icon>
			</v-btn>
		</template>
	</v-data-table>
</template>

<script setup lang="ts" generic="T extends AbstractNamedModel">
import { useI18n } from 'vue-i18n'

withDefaults(defineProps<{
	items: T[]
	viewRoute?: string
	editRoute: string
	deletable?: boolean
}>(), {
	viewRoute: undefined,
	deletable: false
})

defineEmits<(e: 'delete', id: number) => void>()

const i18n = useI18n()

const headers = [
	{
		title: i18n.t('page.TypeManagement.name'),
		key: 'identifyingName'
	},
	{
		title: i18n.t('page.TypeManagement.actions'),
		key: 'actions',
		align: 'end',
		sortable: false
	}
]
</script>
