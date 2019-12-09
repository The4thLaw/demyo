import { Heading, Bold, Italic, Strike, Underline, BulletList,
	OrderedList, ListItem, Link, Blockquote, HardBreak, HorizontalRule, History, Image } from 'tiptap-vuetify'

export const tipTapExtensions = [
	History,
	Blockquote,
	Link,
	Underline,
	Strike,
	Italic,
	Image,
	ListItem,
	BulletList,
	OrderedList,
	[Heading, {
		options: {
			levels: [1, 2, 3]
		}
	}],
	Bold,
	HorizontalRule,
	HardBreak
]
