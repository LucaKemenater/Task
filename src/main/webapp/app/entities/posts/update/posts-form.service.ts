import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPosts, NewPosts } from '../posts.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPosts for edit and NewPostsFormGroupInput for create.
 */
type PostsFormGroupInput = IPosts | PartialWithRequiredKeyOf<NewPosts>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPosts | NewPosts> = Omit<T, 'publishedAt'> & {
  publishedAt?: string | null;
};

type PostsFormRawValue = FormValueOf<IPosts>;

type NewPostsFormRawValue = FormValueOf<NewPosts>;

type PostsFormDefaults = Pick<NewPosts, 'id' | 'publishedAt'>;

type PostsFormGroupContent = {
  id: FormControl<PostsFormRawValue['id'] | NewPosts['id']>;
  title: FormControl<PostsFormRawValue['title']>;
  slug: FormControl<PostsFormRawValue['slug']>;
  content: FormControl<PostsFormRawValue['content']>;
  publishedAt: FormControl<PostsFormRawValue['publishedAt']>;
  status: FormControl<PostsFormRawValue['status']>;
  authorLogin: FormControl<PostsFormRawValue['authorLogin']>;
};

export type PostsFormGroup = FormGroup<PostsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PostsFormService {
  createPostsFormGroup(posts: PostsFormGroupInput = { id: null }): PostsFormGroup {
    const postsRawValue = this.convertPostsToPostsRawValue({
      ...this.getFormDefaults(),
      ...posts,
    });
    return new FormGroup<PostsFormGroupContent>({
      id: new FormControl(
        { value: postsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(postsRawValue.title, {
        validators: [Validators.required],
      }),
      slug: new FormControl(postsRawValue.slug, {
        validators: [Validators.required],
      }),
      content: new FormControl(postsRawValue.content, {
        validators: [Validators.required],
      }),
      publishedAt: new FormControl(postsRawValue.publishedAt, {
        validators: [Validators.required],
      }),
      status: new FormControl(postsRawValue.status, {
        validators: [Validators.required],
      }),
      authorLogin: new FormControl(postsRawValue.authorLogin, {
        validators: [Validators.required],
      }),
    });
  }

  getPosts(form: PostsFormGroup): IPosts | NewPosts {
    return this.convertPostsRawValueToPosts(form.getRawValue() as PostsFormRawValue | NewPostsFormRawValue);
  }

  resetForm(form: PostsFormGroup, posts: PostsFormGroupInput): void {
    const postsRawValue = this.convertPostsToPostsRawValue({ ...this.getFormDefaults(), ...posts });
    form.reset(
      {
        ...postsRawValue,
        id: { value: postsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PostsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      publishedAt: currentTime,
    };
  }

  private convertPostsRawValueToPosts(rawPosts: PostsFormRawValue | NewPostsFormRawValue): IPosts | NewPosts {
    return {
      ...rawPosts,
      publishedAt: dayjs(rawPosts.publishedAt, DATE_TIME_FORMAT),
    };
  }

  private convertPostsToPostsRawValue(
    posts: IPosts | (Partial<NewPosts> & PostsFormDefaults),
  ): PostsFormRawValue | PartialWithRequiredKeyOf<NewPostsFormRawValue> {
    return {
      ...posts,
      publishedAt: posts.publishedAt ? posts.publishedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
