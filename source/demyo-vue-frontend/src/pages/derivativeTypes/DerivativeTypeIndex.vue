<template>
	<div>
		<TextIndex :items="modelList" :split-by-first-letter="false" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/derivativeTypes/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<Fab to="/derivativeTypes/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import typeService from '@/services/derivative-type-service'

const { modelList } = useSimpleIndex(typeService, 'title.index.derivativeType')
</script>
