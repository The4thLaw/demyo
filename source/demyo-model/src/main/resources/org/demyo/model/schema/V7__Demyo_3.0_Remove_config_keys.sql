-- These configuration parameters became useless as part of the Vue update
delete from configuration
	where config_key in ('header.quickLinks', 'paging.albumPageSize', 'thumbnail.width', 'thumbnail.height');
