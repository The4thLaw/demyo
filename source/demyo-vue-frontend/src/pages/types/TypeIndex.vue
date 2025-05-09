<template>
	<v-container id="v-TypeManagement">
		<SectionCard :title="$t('title.index.binding')" :loading="bindingsLoading">
			<TextIndex
				:items="bindings" view-route="BindingView"
				:keyboard-nav="false" :compact="true" :split-by-first-letter="false"
			/>
		</SectionCard>

		<SectionCard :title="$t('title.index.derivativeType')" :loading="derivativeTypesLoading">
			<TextIndex
				:items="derivativeTypes" view-route="DerivativeTypeView"
				:keyboard-nav="false" :compact="true" :split-by-first-letter="false"
			/>
		</SectionCard>

		<SectionCard :title="$t('title.index.bookType')" :loading="bookTypesLoading">
			<template v-if="!bookTypeManagement">
				<v-alert
					border="start" type="info" text
					class="my-4" variant="outlined"
				>
					{{ $t('page.BookType.management.disabled') }}
				</v-alert>
				<v-btn color="secondary" @click="enableBookTypeManagement">
					{{ $t('page.BookType.management.enable') }}
				</v-btn>
			</template>
			<TextIndex
				v-else
				:items="bookTypes" view-route="BookTypeView"
				:keyboard-nav="false" :compact="true" :split-by-first-letter="false"
			/>
		</SectionCard>

		<v-speed-dial
			location="bottom center"
		>
			<template #activator="{ props: activatorProps }">
				<!-- Vuetify's own FAB doesn't work for some reason: the options overlay the base button -->
				<Fab v-bind="activatorProps" icon="mdi-plus" />
			</template>

			<v-btn key="1" to="/bindings/new" icon>
				<v-icon>mdi-notebook</v-icon>
				<v-tooltip :text="$t('menu.bindings.add')" activator="parent" />
			</v-btn>
			<v-btn key="2" to="/derivativeTypes/new" icon>
				<v-icon>mdi-brush</v-icon>
				<v-tooltip :text="$t('menu.derivative_types.add')" activator="parent" />
			</v-btn>
			<v-btn key="3" to="/bookTypes/new" icon>
				<v-icon>mdi-bookshelf</v-icon>
				<v-tooltip :text="$t('menu.book_types.add')" activator="parent" />
			</v-btn>
		</v-speed-dial>
	</v-container>
</template>

<script setup lang="ts">
import bindingService from '@/services/binding-service'
import bookTypeService from '@/services/book-type-service'
import derivativeTypeService from '@/services/derivative-type-service'

// Bindings
const bindings = ref([] as Binding[])
const bindingsLoading = ref(true)
void (async (): Promise<void> => {
	bindings.value = await bindingService.findForIndex()
	bindingsLoading.value = false
})()

// Derivative types
const derivativeTypes = ref([] as DerivativeType[])
const derivativeTypesLoading = ref(true)
void (async (): Promise<void> => {
	derivativeTypes.value = await derivativeTypeService.findForIndex()
	derivativeTypesLoading.value = false
})()

// Book types
const bookTypeManagement = ref(false)
const bookTypes = ref([] as BookType[])
const bookTypesLoading = ref(true)
async function loadBookTypes(): Promise<void> {
	bookTypesLoading.value = true
	bookTypeManagement.value = await bookTypeService.isManagementEnabled()
	bookTypes.value = await bookTypeService.findForIndex()
	bookTypesLoading.value = false
}
void loadBookTypes()

async function enableBookTypeManagement(): Promise<void> {
	await bookTypeService.enableManagement()
	await loadBookTypes()
}
</script>
