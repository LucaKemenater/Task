import dayjs from 'dayjs/esm';
import { PostStatus } from 'app/entities/enumerations/post-status.model';

export interface IPosts {
  id: number;
  title?: string | null;
  slug?: string | null;
  content?: string | null;
  publishedAt?: dayjs.Dayjs | null;
  status?: keyof typeof PostStatus | null;
  authorLogin?: string | null;
}

export type NewPosts = Omit<IPosts, 'id'> & { id: null };
