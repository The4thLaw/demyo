<template>
	<div>
		<v-container fluid class="v-TagIndex__list">
			<SectionCard>
				<TagLink :model="tags" />
			</SectionCard>
		</v-container>
		<Fab v-if="!loading" to="/tags/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import tagService from '@/services/tag-service'

function getMaxUsageCount(tagsToCount: Tag[]): number {
	if (tagsToCount.length === 0) {
		return 0
	}

	const max = Math.max(...tagsToCount.map(t => t.usageCount))
	// Have a reasonable minimum else it just looks silly if there are few tags and they are seldom used
	return Math.max(max, 10)
}

async function fetchData(): Promise<ProcessedTag[]> {
	const loadedTags = await tagService.findForIndex() as ProcessedTag[]

	// Post-process tags: compute the relative weight in %
	// (the base is 100% and the max is 200%, which is very convenient for the font-size)
	const maxUsageCount = getMaxUsageCount(loadedTags)
	for (const tag of loadedTags) {
		tag.relativeWeight = Math.round(100 * tag.usageCount / maxUsageCount + 100)
	}

	return Promise.resolve(loadedTags)
}

const { loading, modelList: tags } = useSimpleIndex(tagService, 'title.index.tag', fetchData)
</script>

<style lang="scss">
.v-TagIndex__list {
	line-height: 275%;
	text-align: center;
}
</style>
