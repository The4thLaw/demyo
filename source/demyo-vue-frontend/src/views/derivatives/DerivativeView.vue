<template>
	<v-container>
		<AppTasks v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.derivative')"
				:to="`/derivatives/${derivative.id}/edit`"
				icon="mdi-pencil"
			/>
			<!--
			Adding an @click="appTasksMenu = false" causes the dialog to instantly
			disappear because the AppTask isn't rendered anymore
			-->
			<AppTask
				:label="$t('quickTasks.delete.derivative')"
				:confirm="$t('quickTasks.delete.derivative.confirm')"
				icon="mdi-account-minus"
				@cancel="appTasksMenu = false"
				@confirm="deleteDerivative"
			/>
			<!-- TODO: icon -->
			<AppTask
				:label="$t('quickTasks.add.images.to.derivative')"
				icon="mdi-pencil"
				@click="appTasksMenu = false; dndDialog = true"
			/>
			<!-- TODO: other tasks ? -->
		</AppTasks>
		<DnDImage v-model="dndDialog" other-images-label="field.Derivative.images" @save="saveDndImages" />
		Foo
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import DnDImage from '@/components/DnDImage'
import derivativeService from '@/services/derivative-service'

export default {
	name: 'DerivativeView',

	components: {
		AppTask,
		AppTasks,
		DnDImage
	},

	data() {
		return {
			appTasksMenu: false,
			dndDialog: false,
			derivative: {}
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
			//this.mainLoading = true
			//const id = parseInt(this.$route.params.id, 10)

			/*
			this.derivative = await derivativeService.findById(id)
			this.mainLoading = false
			*/
		},

		async saveDndImages(data) {
			// TODO: use derivative.id instead of parsing
			const id = parseInt(this.$route.params.id, 10)
			let ok = await derivativeService.saveFilepondImages(id, data.otherImages)
			if (ok) {
				this.$store.dispatch('ui/showSnackbar', this.$t('draganddrop.snack.confirm'))
				// TODO: refresh the data to show the images
			} else {
				this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
			}
		},

		deleteDerivative() {
			// TODO
		}
	}
}
</script>
