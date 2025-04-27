<template>
	<div id="v-TypeManagement">
		<SectionCard :title="$t('title.index.binding')" :loading="bindingsLoading">
			<TypeIndex
				view-route="BindingView" edit-route="BindingEdit"
				:items="bindings"
			/>
		</SectionCard>

		<SectionCard :title="$t('title.index.derivativeType')" :loading="derivativeTypesLoading">
			<TypeIndex
				view-route="DerivativeTypeView" edit-route="DerivativeTypeEdit"
				:items="derivativeTypes"
			/>
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
			<TypeIndex
				v-else
				edit-route="BookTypeEdit" :items="bookTypes" deletable
				@delete="deleteBookType"
			/>
		</SectionCard>

		<v-speed-dial
			location="bottom center"
		>
			<template #activator="{ props: activatorProps }">
				<!-- Vuetify's own FAB doesn't work for some reason: the options overlay the base button -->
				<Fab v-bind="activatorProps" icon="mdi-plus" />
			</template>

			<v-tooltip key="1" :text="$t('menu.bindings.add')">
				<template #activator="{ props }">
					<v-btn key="1" to="/bindings/new" icon="mdi-notebook" v-bind="props" />
				</template>
			</v-tooltip>
			<v-tooltip key="2" :text="$t('menu.derivative_types.add')">
				<template #activator="{ props }">
					<v-btn key="1" to="/derivativeTypes/new" icon="mdi-brush" v-bind="props" />
				</template>
			</v-tooltip>
			<v-tooltip key="3" :text="$t('menu.book_types.add')">
				<template #activator="{ props }">
					<v-btn key="1" to="/bookTypes/new" icon="mdi-bookshelf" v-bind="props" />
				</template>
			</v-tooltip>
		</v-speed-dial>
	</div>
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
	bookTypeManagement.value = true
}

function deleteBookType(): void {
	// TODO: delete and reassign
	// Reload
	void loadBookTypes()
}
</script>
