<template>
	<v-container>
		<SectionCard :loading="mainLoading">
			<div class="a">
				<div v-if="author.portrait">
					<!--
					What we want for this image is:
						- On large screens, be side to side with the text
						- On medium screens, be a bit smaller
						- On small screens, be above the text and use the small version
							(so that the height is reduced as well)
						- Be nice on HiDPI and LoDPI screens
					-->
					<!--
						TODO: extract to component and add a lightbox
						-->
					<img
						:src="`${basePortraitUrl}?w=200`"
						:srcset="`
							${basePortraitUrl}?w=200 200w
							${basePortraitUrl}?w=400 400w,
							${basePortraitUrl}?w=700 700w,`"
						sizes="(max-width: 700px) 200px, 350px"
					>
				</div>
				<div>
					<h1 class="display-1">{{ author.identifyingName }}</h1>
					<FieldValue :label="$t('field.Author.website')" :value="author.website">
						<a :href="author.website">{{ author.website }}</a>
					</FieldValue>
					<FieldValue :label="$t('field.Author.biography')" :value="author.biography">
						<div v-html="author.biography" />
					</FieldValue>
				</div>
			</div>
		</SectionCard>
	</v-container>
</template>

<script>
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorView',

	components: {
		FieldValue,
		SectionCard
	},

	// TODO: title.author.view ?
	metaInfo() {
		return {
			title: this.author.identifyingName
		}
	},

	data() {
		return {
			mainLoading: true,
			author: {}
		}
	},

	computed: {
		basePortraitUrl() {
			let encodedName = encodeURI(this.author.portrait.userFileName)
			return '/images/' + this.author.portrait.id + '/file/' + encodedName
		}
	},

	watch: {
		'$route': 'fetchData'
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.mainLoading = true
			const id = parseInt(this.$route.params.id, 10)
			this.author = await authorService.findById(id)
			this.mainLoading = false
		}
	}
}
</script>

<style lang="less">
.a {
	display: flex;
	> div {
		//flex-grow: 1;
	}
}

@media (max-width: 550px) {
	.a {
		flex-direction: column;
		> div:first-child {
			text-align: center;
		}
	}
}
</style>
