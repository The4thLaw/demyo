<template>
	<v-dialog
		v-model="dialog" :persistent="requireSelection" max-width="400px"
		@click:outside="$emit('cancel')"
	>
		<v-card>
			<v-card-title>
				{{ $t('page.Reader.select.title') }}
			</v-card-title>
			<v-card-text>
				<p>
					{{ $t('page.Reader.select.explanation.base') }}
				</p>
				<v-list>
					<v-list-item v-for="reader in readers" :key="reader.id" @click="select(reader)">
						<v-list-item-icon>
							<LetterIcon :letter="reader.identifyingName.charAt(0)" :color="reader.colour" />
						</v-list-item-icon>
						<v-list-item-content>
							{{ reader.identifyingName }}
						</v-list-item-content>
					</v-list-item>
				</v-list>
			</v-card-text>

			<v-card-actions v-if="!requireSelection">
				<v-spacer />

				<v-btn color="primary" text @click="dialog = false; $emit('cancel')">
					{{ $t('quickTasks.confirm.cancel.label') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>

<script>
import LetterIcon from '@/components/LetterIcon.vue'
import readerService from '@/services/reader-service'

export default {
	name: 'ReaderSelection',

	components: {
		LetterIcon
	},

	props: {
		requireSelection: {
			type: Boolean,
			default: false
		}
	},

	data() {
		return {
			dialog: true,
			readers: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.readers = await readerService.findForIndex()
		},

		async select(reader) {
			await readerService.setCurrentReader(reader)
			this.$emit('select')
		}
	}
}
</script>
