<template>
	<v-card :flat="true" :hover="true" class="c-MetaSeriesCard">
		<template v-if="meta.album">
			<h3 class="c-MetaSeriesCard__title">{{meta.album.title}}</h3>
			one shot
		</template>
		<template v-if="meta.series">
			series
		</template>
	</v-card>
</template>

<script>
export default {
	name: 'MetaSeriesCard',

	props: {
		meta: {
			type: null,
			required: true
		}
	}
}
</script>

<style lang="less">
.c-MetaSeriesCard__title {
	font-size: inherit;
	font-weight: 600;
}
</style>
