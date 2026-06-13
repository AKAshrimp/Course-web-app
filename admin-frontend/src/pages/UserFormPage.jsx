import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { Button, Card, Form, Input, Select, Space, message } from "antd";

import { createUser, getUser, updatePassword, updateUser } from "../api/adminUsersApi";
import AdminLayout from "../layout/AdminLayout";

export default function UserFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = Boolean(id);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!isEditing) {
      return;
    }

    getUser(id)
      .then((user) => {
        form.setFieldsValue({
          username: user.username,
          password: "",
          fullName: user.fullName,
          email: user.email,
          phoneNumber: user.phoneNumber || "",
          roles: user.roles || ["ROLE_STUDENT"]
        });
      })
      .catch(() => message.error("Failed to load user."));
  }, [form, id, isEditing]);

  async function handleSubmit(values) {
    setLoading(true);

    try {
      if (isEditing) {
        await updateUser(id, values);
        if (values.password) {
          await updatePassword(id, values.password);
        }
      } else {
        await createUser(values);
      }

      message.success("User saved");
      navigate("/users");
    } catch (error) {
      message.error("Failed to save user.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <AdminLayout
      title={isEditing ? "Edit User" : "New User"}
      subtitle={isEditing ? "Update account details and roles" : "Create a new course website account"}
    >
      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{ roles: ["ROLE_STUDENT"] }}
          className="user-form"
        >
          <Form.Item
            label="Username"
            name="username"
            rules={[{ required: true, message: "Username is required" }]}
          >
            <Input disabled={isEditing} />
          </Form.Item>

          <Form.Item
            label={isEditing ? "New password" : "Password"}
            name="password"
            rules={isEditing ? [] : [{ required: true, message: "Password is required" }]}
          >
            <Input.Password placeholder={isEditing ? "Leave blank to keep current password" : ""} />
          </Form.Item>

          <Form.Item
            label="Full name"
            name="fullName"
            rules={[{ required: true, message: "Full name is required" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: "Email is required" },
              { type: "email", message: "Enter a valid email" }
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item label="Phone number" name="phoneNumber">
            <Input />
          </Form.Item>

          <Form.Item label="Roles" name="roles">
            <Select
              mode="multiple"
              options={[
                { label: "Student", value: "ROLE_STUDENT" },
                { label: "Teacher", value: "ROLE_TEACHER" }
              ]}
            />
          </Form.Item>

          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              Save
            </Button>
            <Button onClick={() => navigate("/users")}>Cancel</Button>
          </Space>
        </Form>
      </Card>
    </AdminLayout>
  );
}
