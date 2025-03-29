<template>
	<div>
		<TextIndex :items="modelList" :split-by-first-letter="false" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/bindings/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<Fab to="/bindings/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import bindingService from '@/services/binding-service'

const { modelList } = useSimpleIndex(bindingService, 'title.index.binding')
</script>
