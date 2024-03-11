// This file is processed and oaded automatically before all test files.
//
// This is a great place to put global configuration and behavior that modifies Cypress.

import './commands'

before(() => {
	cy.initDatabase().then(() => {
		cy.selectReader()
	})
})

// TODO: https://github.com/The4thLaw/demyo/issues/20
