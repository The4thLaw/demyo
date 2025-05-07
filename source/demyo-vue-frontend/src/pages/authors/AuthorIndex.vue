<template>
	<div>
		<TextIndex
			view-route="AuthorView" :items="modelList" :first-letter-extractor="(item: Author) => item.name[0]"
			@page-change="scrollToTop"
		/>
		<Fab to="/authors/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import authorService from '@/services/author-service'

const { modelList } = useSimpleIndex(authorService, 'title.index.author')
</script>
