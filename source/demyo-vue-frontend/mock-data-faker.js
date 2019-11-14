let faker = require('faker')
let actualData = require('./mock-data-actual.json')

faker.seed(42)

function luckRatio(ratio) {
	let number = (faker.random.number() % 10 + 1) / 10
	return number <= ratio
}

// AUTHORS
let authors = []
for (let i = 0; i < 250; i++) {
	let lname = faker.name.lastName()
	let fname = faker.name.firstName()
	let author = {
		id: i,
		name: lname,
		firstName: fname,
		identifyingName: `${fname} ${lname}`
	}
	if (luckRatio(0.7)) {
		let nick = faker.name.jobTitle()
		author.nickname = nick
		author.identifyingName = `${fname} '${nick}' ${lname}`
	}
	if (luckRatio(0.1)) {
		author.biography = `<p>${faker.lorem.paragraph()}</p>`
	}
	authors.push(author)
}

module.exports = () => {
	return {
		authors: authors,
		translations_en: actualData.translations_en,
		translations_fr: actualData.translations_fr
	}
}
