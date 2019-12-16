<template>
	<v-container>
		<SectionCard title="title.add.image.detect">
			Taiste
		</SectionCard>
	</v-container>
</template>

<script>
import SectionCard from '@/components/SectionCard'

export default {
	name: 'ImageDetect',

	components: {
		SectionCard
	},

	data() {
		return {
			detecting: true,
			detectedImages: []
		}
	}
}
</script>
