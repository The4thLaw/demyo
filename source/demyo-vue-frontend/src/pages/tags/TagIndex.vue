<template>
	<div>
		<v-container fluid class="v-TagIndex__list">
			<SectionCard>
				<TagLink :model="tags" />
			</SectionCard>
		</v-container>
		<v-btn
			fab to="/tags/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import SectionCard from '@/components/SectionCard.vue'
import TagLink from '@/components/TagLink.vue'
import tagService from '@/services/tag-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'TagIndex',

	components: {
		SectionCard,
		TagLink
	},

	metaInfo() {
		return {
			title: this.$t('title.index.tag')
		}
	},

	data() {
		return {
			tags: []
		}
	},

	computed: {
		maxUsageCount() {
			if (this.tags.length === 0) {
				return 0
			}

			const max = Math.max.apply(Math, this.tags.map(t => t.usageCount))
			// Have a reasonable minimum else it just looks silly if there are few tags and they are seldom used
			return Math.max(max, 10)
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			this.tags = await tagService.findForIndex()

			// Post-process tags: compute the relative weight in %
			// (the base is 100% and the max is 200%, which is very convenient for the font-size)
			for (const tag of this.tags) {
				tag.relativeWeight = Math.round(100 * tag.usageCount / this.maxUsageCount + 100)
			}

			uiStore.disableGlobalOverlay()
		}
	}
}
</script>

<style lang="less">
.v-TagIndex__list {
	line-height: 275%;
	text-align: center;
}
</style>
