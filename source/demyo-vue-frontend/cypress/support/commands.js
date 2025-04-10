Cypress.Commands.add('initDatabase', () => {
	return cy.fixture('demyo-cypress-data-set.dea', 'binary')
		.then(binary => Cypress.Blob.binaryStringToBlob(binary))
		.then(blob => {
			const formdata = new FormData()
			formdata.append('importFile', blob, 'demyo-cypress.dea')

			cy.request({
				url: '/api/manage/import',
				method: 'POST',
				headers: {
					'Content-Type': 'multipart/form-data'
				},
				body: formdata
			}).its('status').should('be.equal', 200)
		})
})

Cypress.Commands.add('selectReader', () => {
	return cy.request({
		method: 'GET',
		url: '/api/readers/1'
	})
		.then(resp => {
			console.log('Cypress reader is', resp.body)
			window.localStorage.setItem('currentReader', JSON.stringify(resp.body))
		})
})

Cypress.Commands.add('getInputByLabel', (label) => {
	return cy.contains(label).parents('.v-input').find('input')
})

Cypress.Commands.add('openQuickTasks', () => {
	cy.get('.v-app-bar i.mdi-dots-vertical').click()
})

Cypress.Commands.add('assertTitle', (title) => {
	cy.title().should('include', `${title} – Demyo`)
	cy.get('.v-toolbar-title').should('have.text', title)
})
