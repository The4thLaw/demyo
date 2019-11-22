<template>
	<v-card :loading="loading ? 'primary' : false">
		<div class="pa-6">
			<template v-if="!image">
				<slot />
			</template>
			<div v-if="image" class="c-SectionCard__container">
				<div class="c-SectionCard__image">
					<!--
					What we want for this image is:
						- On large screens, be side to side with the text
						- On medium screens, be a bit smaller
						- On small screens, be above the text and use the small version
							(so that the height is reduced as well)
						- Be nice on HiDPI and LoDPI screens
					-->
					<!-- TODO: use the right URL (with missing width, probably) -->
					<img
						v-img="{src: `${baseImageUrl}?w=1200`}"
						:src="`${baseImageUrl}?w=200`"
						:srcset="`
							${baseImageUrl}?w=200 200w
							${baseImageUrl}?w=400 400w,
							${baseImageUrl}?w=700 700w,`"
						sizes="(max-width: 700px) 200px, 350px"
					>
				</div>
				<div class="c-SectionCard__content">
					<slot />
				</div>
			</div>
		</div>
	</v-card>
</template>

<script>
/**
 * A card used to section content.
 * Sports standard padding and loading style.
 */
export default {
	name: 'SectionCard',

	props: {
		loading: {
			type: Boolean,
			required: false,
			default: false
		},

		image: {
			type: Object,
			required: false,
			default: null
		}
	},

	computed: {
		baseImageUrl() {
			let encodedName = encodeURI(this.image.userFileName)
			return '/images/' + this.image.id + '/file/' + encodedName
		}
	}
}
</script>

<style lang="less">
.c-SectionCard__container {
	display: flex;
}

.c-SectionCard__content {
	padding-left: 24px;
}

@media (max-width: 550px) {
	.c-SectionCard__container {
		flex-direction: column;
	}

	.c-SectionCard__image {
		text-align: center;
	}

	.c-SectionCard__content {
		padding-left: 0;
	}
}
</style>
