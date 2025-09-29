<template>
	<div>
		<CardTextIndex
			:items="modelList" :first-letter-extractor="(item: Publisher) => item.identifyingName[0]"
			@page-change="scrollToTop"
		>
			<template #default="slotProps">
				<PublisherCard :publisher="slotProps.item" />
			</template>
		</CardTextIndex>

		<v-speed-dial
			location="bottom center"
		>
			<template #activator="{ props: activatorProps }">
				<!-- Vuetify's own FAB doesn't work for some reason: the options overlay the base button -->
				<Fab v-bind="activatorProps" icon="mdi-plus" />
			</template>

			<v-btn key="1" to="/publishers/new" icon>
				<v-icon>mdi-office-building</v-icon>
				<v-tooltip :text="$t('menu.publishers.add')" activator="parent" />
			</v-btn>
			<v-btn key="2" to="/collections/new" icon>
				<v-icon>mdi-folder-multiple</v-icon>
				<v-tooltip :text="$t('menu.collections.add')" activator="parent" />
			</v-btn>
		</v-speed-dial>
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import publisherService from '@/services/publisher-service'

const { modelList } = useSimpleIndex(publisherService, 'title.index.publisher')
</script>
