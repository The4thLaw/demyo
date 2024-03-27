<template>
	<div>
		<TextIndex :items="modelList" :first-letter-extractor="(item) => item.name[0]" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/authors/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<Fab to="/authors/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { useSimpleIndex } from '@/composables/model-index'
import authorService from '@/services/author-service'

const { modelList } = useSimpleIndex(authorService, 'title.index.author')
</script>
