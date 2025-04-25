<template>
	<div id="v-TypeManagement">
		<SectionCard :title="$t('title.index.binding')">
			TODO
		</SectionCard>

		<SectionCard :title="$t('title.index.derivativeType')">
			TODO
		</SectionCard>

		<SectionCard :title="$t('title.index.bookType')" :loading="bookTypeManagement && bookTypesLoading">
			<template v-if="!bookTypeManagement">
				<v-alert border="start" type="info" text class="my-4">
					{{ $t('page.BookType.management.disabled') }}

				</v-alert>
				<v-btn color="secondary" @click="enableBookTypeManagement">
					{{ $t('page.BookType.management.enable') }}
				</v-btn>
			</template>
			<template v-else>
				<v-data-table :items="bookTypes" :headers="headers" :hide-default-footer="bookTypes.length < 11">
					<template v-slot:item.actions="{ item }">
						<v-btn :to="`/bookTypes/${item.id}/edit`" icon variant="text">
							<v-icon>mdi-pencil</v-icon>
						</v-btn>
						<!-- TODO: delete and reassign -->
						<v-btn :to="`/bookTypes/${item.id}/edit`" icon variant="text">
							<v-icon>mdi-delete</v-icon>
						</v-btn>
					</template>
				</v-data-table>
			</template>
		</SectionCard>

		<v-speed-dial
			location="bottom center"
		>
			<template v-slot:activator="{ props: activatorProps }">
				<!-- Vuetify's own FAB doesn't work for some reason: the options overlay the base button -->
				<Fab v-bind="activatorProps" icon="mdi-plus"></Fab>
			</template>

			<v-tooltip key="1" :text="$t('menu.bindings.add')">
				<template v-slot:activator="{ props }">
					<v-btn key="1" to="/bindings/new" icon="mdi-notebook" v-bind="props" />
				</template>
			</v-tooltip>
			<v-tooltip key="2" :text="$t('menu.derivative_types.add')">
				<template v-slot:activator="{ props }">
					<v-btn key="1" to="/derivativeTypes/new" icon="mdi-brush" v-bind="props" />
				</template>
			</v-tooltip>
			<v-tooltip key="3" :text="$t('menu.book_types.add')">
				<template v-slot:activator="{ props }">
					<v-btn key="1" to="/bookTypes/new" icon="mdi-bookshelf" v-bind="props" />
				</template>
			</v-tooltip>
		</v-speed-dial>
	</div>
</template>

<script setup lang="ts">
import bookTypeService from '@/services/book-type-service'
import { useRefDataStore } from '@/stores/ref-data'
import { useI18n } from 'vue-i18n'

const i18n = useI18n()
const refDataStore = useRefDataStore()

const bookTypeManagement = computed(() => refDataStore.bookTypeManagement)

const headers = [
	{ title: i18n.t('field.BookType.name'), key: 'identifyingName' },
	{ title: i18n.t('page.TypeManagement.actions'), key: 'actions', align: 'end', sortable: false }
]


const bookTypes = ref([] as BookType[])
const bookTypesLoading = ref(true)
async function loadBookTypes() {
	bookTypes.value = await bookTypeService.findForIndex()
	bookTypesLoading.value = false
}
loadBookTypes()

async function enableBookTypeManagement() {
	await bookTypeService.enableManagement()
	refDataStore.refreshBookTypeManagement()
}
</script>
