import dayjs from 'dayjs/esm';

import { IPosts, NewPosts } from './posts.model';

export const sampleWithRequiredData: IPosts = {
  id: 10701,
  title: 'story',
  slug: 'safe crumble sniveling',
  content: '../fake-data/blob/hipster.txt',
  publishedAt: dayjs('2025-09-21T14:35'),
  status: 'PUBLISHED',
  authorLogin: 'wisecrack likewise',
};

export const sampleWithPartialData: IPosts = {
  id: 9725,
  title: 'glittering oh deeply',
  slug: 'turret definite mould',
  content: '../fake-data/blob/hipster.txt',
  publishedAt: dayjs('2025-09-21T07:37'),
  status: 'DRAFT',
  authorLogin: 'rowdy guacamole',
};

export const sampleWithFullData: IPosts = {
  id: 25459,
  title: 'selfishly whereas nor',
  slug: 'so',
  content: '../fake-data/blob/hipster.txt',
  publishedAt: dayjs('2025-09-21T13:30'),
  status: 'DRAFT',
  authorLogin: 'boohoo preregister',
};

export const sampleWithNewData: NewPosts = {
  title: 'strong',
  slug: 'mushy',
  content: '../fake-data/blob/hipster.txt',
  publishedAt: dayjs('2025-09-20T21:19'),
  status: 'DRAFT',
  authorLogin: 'infatuated fedora guest',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
