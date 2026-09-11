import axiosClient from './axiosClient';
import type { Course, PagedResponse } from '../types/course';

export const getCourses = (keyword?: string, page: number = 0, size: number = 10) => {
  return axiosClient.get<PagedResponse<Course>>('/api/courses', {
    params: { keyword, page, size },
  });
};
