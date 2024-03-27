<template>
	<div>
		<TextIndex :items="modelList" :split-by-first-letter="false" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/readers/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<Fab to="/readers/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { useSimpleIndex } from '@/composables/model-index'
import readerService from '@/services/reader-service'

const { modelList } = useSimpleIndex(readerService, 'title.index.reader')
</script>
