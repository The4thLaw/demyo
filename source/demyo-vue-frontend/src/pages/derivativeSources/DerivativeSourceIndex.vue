<template>
	<div>
		<TextIndex
			:items="modelList" :first-letter-extractor="(item) => item.identifyingName[0]" @page-change="scrollToTop"
		>
			<template #default="slotProps">
				<router-link :to="`/derivativeSources/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<Fab to="/derivativeSources/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { useSimpleIndex } from '@/composables/model-index'
import sourceService from '@/services/derivative-source-service'

const { modelList } = useSimpleIndex(sourceService)
</script>
