<template>
	<div class="c-FormActions">
		<v-btn color="accent" class="c-FormActions__button" @click="$emit('save')">
			{{ $t('button.save') }}
		</v-btn>
		<v-btn
			v-if="showReset" text color="primary"
			class="c-FormActions__button" @click="$emit('reset')"
		>
			{{ $t('button.reset') }}
		</v-btn>
	</div>
</template>

<script>
export default {
	name: 'FormActions',

	props: {
		showReset: {
			type: Boolean,
			default: true
		}
	}
}
</script>

<style lang="less">
.c-FormActions {
	text-align: center;

	&__button {
		margin-right: 16px;
	}
}
</style>
S
